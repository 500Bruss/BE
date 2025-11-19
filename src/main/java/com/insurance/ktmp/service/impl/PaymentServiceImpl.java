package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.JsonUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.PaymentCreationRequest;
import com.insurance.ktmp.dto.response.PaymentResponse;
import com.insurance.ktmp.entity.Application;
import com.insurance.ktmp.entity.Payment;
import com.insurance.ktmp.enums.ApplicationStatus;
import com.insurance.ktmp.enums.PaymentMethod;
import com.insurance.ktmp.enums.PaymentStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.repository.ApplicationRepository;
import com.insurance.ktmp.repository.PaymentRepository;
import com.insurance.ktmp.service.IPaymentService;
import com.insurance.ktmp.service.IPolicyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {
    private final PaymentRepository paymentRepository;
    private final ApplicationRepository applicationRepository;
    private final VnPayService vnPayService;
    private final IPolicyService policyService;

    @Value("${vnpay.secretKey}")
    String secretKey;

    @Override
    public RestResponse<PaymentResponse> createPayment(String ipAddress, Long customerId, PaymentCreationRequest request) {
        log.info("Start creating payment for orderId={} by customer={}", request.getApplicationId(), customerId);

        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getUser().getId().equals(customerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        if (application.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE);
        }

        Payment payment = Payment.builder()
                .id(IdGenerator.generateRandomId())
                .application(application)
                .quote(application.getQuote())
                .user(application.getUser())
                .paymentMethod(PaymentMethod.valueOf(request.getMethod().name()))
                .amount(application.getTotalPremium())
                .providerReference(PaymentMethod.VNPAY.name())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        return RestResponse.ok(handleVpnPayment(payment, ipAddress));
    }

    private PaymentResponse handleVpnPayment(Payment payment, String ipAddress) {
        String locale = "vn";
        String orderInfo = "Thanh toan don hang: " + payment.getApplication().getId();
        String paymentUrl = vnPayService.buildPaymentUrl(payment.getId(),
                payment.getApplication().getTotalPremium(), ipAddress, locale, orderInfo);

        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(null);
        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .applicationId(payment.getApplication().getId())
                .quoteId(payment.getQuote().getId())
                .userId(payment.getUser().getId())
                .userName(payment.getUser().getFullName())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .providerReference(payment.getProviderReference())
                .status(payment.getStatus())
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public RestResponse<String> handleVnPayIpn(HttpServletRequest request) {
        Map<String, String> params = extractVnpParams(request);
        log.info("Received VNPAY IPN callback: {}", params);

        //Kiểm tra chữ ký bảo mật
        String vnpSecureHash = params.remove("vnp_SecureHash");
        String signData = vnPayService.buildHashData(params);
        String expectedHash = vnPayService.hmacSHA512(secretKey, signData);

        if (!expectedHash.equalsIgnoreCase(vnpSecureHash)) {
            log.warn("VNPAY invalid signature: {}", vnpSecureHash);
            return RestResponse.badRequest("Invalid signature",  vnpSecureHash);
        }

        Long paymentId = Long.valueOf(params.get("vnp_TxnRef"));
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        Application application = payment.getApplication();

        // 3️⃣ Check idempotency (đã xử lý rồi thì bỏ qua)
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return RestResponse.ok("{\"RspCode\":\"00\",\"Message\":\"Already confirmed\"}");
        }

        // Xử lý logic cập nhật đơn hàng & thanh toán
        if ("00".equals(responseCode)) {
            log.info("vào");
//            handlePaymentSuccess(payment, order, params);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transactionNo);
            payment.setTransactionTime(LocalDateTime.now());
            payment.setVnpayResp(JsonUtil.toJson(params));

            application.setStatus(ApplicationStatus.APPROVED);
            application.setUpdatedAt(LocalDateTime.now());
        } else {
            //todo
//            handlePaymentFailure(payment, order);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionTime(LocalDateTime.now());
            payment.setTransactionId(transactionNo);

            application.setStatus(ApplicationStatus.CANCELLED);
            application.setUpdatedAt(LocalDateTime.now());
        }

        paymentRepository.save(payment);
        applicationRepository.save(application);

        policyService.createPolicy(application.getId(), 898454043L);

        return RestResponse.ok("Thanh toán " + ("00".equals(responseCode) ? "thành công" : "thất bại"));
    }


    private Map<String, String> extractVnpParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            if (name.startsWith("vnp_")) {
                params.put(name, request.getParameter(name));
            }
        }
        return params;
    }
}
