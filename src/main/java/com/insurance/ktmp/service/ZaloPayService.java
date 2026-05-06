package com.insurance.ktmp.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface ZaloPayService {
    Map<String, Object> createOrder(Long paymentId, Long amount) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;

    String hmacSHA256(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException;
}
