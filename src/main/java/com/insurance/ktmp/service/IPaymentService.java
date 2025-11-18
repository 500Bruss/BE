package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.PaymentCreationRequest;
import com.insurance.ktmp.dto.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IPaymentService {
    RestResponse<PaymentResponse> createPayment(String ipAddress, Long customerId, PaymentCreationRequest request);

    RestResponse<String> handleVnPayIpn(HttpServletRequest request);
}
