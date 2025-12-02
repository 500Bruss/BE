package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class VnPayGatewayController {
    private final IPaymentService paymentService;

    @GetMapping("/vnpay/return")
    @Transactional
    public void vnpayHandleIpn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RestResponse<String> ipnResponse = paymentService.handleVnPayIpn(request); //todo

        if (ipnResponse.status() == HttpStatus.OK.value()) {
            // Nếu thanh toán thành công, redirect người dùng về returnUrl
            String returnUrl = "http://localhost:5173/PaymentResult";  // returnUrl đã được cấu hình trong hệ thống của bạn
            response.sendRedirect(returnUrl); // Redirect về URL cho người dùng
        } else {
            // Nếu có lỗi hoặc thanh toán thất bại, bạn có thể redirect về trang thất bại khác
            String failureUrl = "http://localhost:5173/payment-failed";  // URL khi thanh toán thất bại
            response.sendRedirect(failureUrl); // Redirect về trang thất bại
        }
    }

    @GetMapping("/vnpay/ipn")
    @Transactional
    public ResponseEntity<RestResponse<String>> vnpayHandleIpn(HttpServletRequest request) {
        RestResponse<String> response = paymentService.handleVnPayIpn(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
