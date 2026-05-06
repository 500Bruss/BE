package com.insurance.ktmp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.service.ZaloPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ZaloPayServiceImpl implements ZaloPayService {
    private String appId = "2553";
    private String key1 = "sdng9h9186483jh5u291";
    private String endpoint = "https://sb-openapi.zalopay.vn/v2/create";

    @Override
    public Map<String, Object> createOrder(Long paymentId, Long amount) {
        // Không cần băm MAC hay gọi RestTemplate nữa
        Map<String, Object> mockResult = new HashMap<>();

        // Giả lập link dẫn đến trang thanh toán demo trên FE của bạn
        // Ví dụ: http://localhost:3000/mock-zalopay-ui?paymentId=...
        String mockOrderUrl = "http://localhost:5173/payment/pay/" + paymentId;

        mockResult.put("return_code", 1);
        mockResult.put("order_url", mockOrderUrl);
        mockResult.put("return_message", "Cấp link thanh toán demo thành công!");

        return mockResult;
    }

    @Override
    public String hmacSHA256(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash); // Sử dụng Apache Commons Codec hoặc tự build chuỗi Hex
    }

    private String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }
}