package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.PaymentCreationRequest;
import com.insurance.ktmp.dto.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface IPaymentService {
    RestResponse<PaymentResponse> createPayment(String ipAddress, Long customerId, PaymentCreationRequest request) throws UnsupportedEncodingException;

//    RestResponse<String> handleVnPayIpn(HttpServletRequest request);

    String handleZaloPayCallback(String jsonStr);
}
