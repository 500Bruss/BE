package com.insurance.ktmp.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.entity.Payment;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Slf4j
@Service
public class VnPayService {
    @Value("${vnpay.tmnCode}")
    private String tmnCode;
    @Value("${vnpay.secretKey}")
    private String secretKey;
    @Value("${vnpay.paymentUrl}")
    private String paymentUrl;
    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Build URL thanh toán để redirect khách hàng
     */
    public String buildPaymentUrl(Long paymentId, java.math.BigDecimal amount, String clientIp, String locale, String orderInfo) throws UnsupportedEncodingException {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount.multiply(java.math.BigDecimal.valueOf(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(paymentId));
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", locale != null ? locale : "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", clientIp);

        // Thời gian tạo giao dịch
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // THÊM THAM SỐ THIẾU: vnp_ExpireDate (Thời gian hết hạn - ví dụ 15 phút)
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Tiếp tục logic sắp xếp và băm như cũ...
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Encode cả key và value cho query, chỉ value cho hashData (theo v2.1.0)
                String encodedKey = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()).replace("+", "%20");

                query.append(encodedKey).append('=').append(encodedValue);
                hashData.append(fieldName).append('=').append(encodedValue);

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnp_SecureHash = hmacSHA512(secretKey, hashData.toString());

        return paymentUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    /**
     * Dùng để kiểm tra chữ ký khi nhận phản hồi từ VNPay (IPN/Return)
     * KHÔNG được encode lại value vì VNPay đã gửi về chuỗi đã encode sẵn
     */
    public String buildHashDataRaw(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = fieldNames.iterator(); it.hasNext();) {
            String fieldName = it.next();
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                sb.append(fieldName).append("=").append(fieldValue);
                if (it.hasNext()) sb.append("&");
            }
        }
        return sb.toString();
    }

    public String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) throw new NullPointerException();
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
