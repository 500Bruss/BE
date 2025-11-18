package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class VnPayGatewayController {
    private final IPaymentService paymentService;

    @GetMapping("/vnpay/return")
    @Transactional
    public ResponseEntity<RestResponse<String>> vnpayReturn(HttpServletRequest request) {
        RestResponse<String> response = paymentService.handleVnPayIpn(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/vnpay/ipn")
    @Transactional
    public ResponseEntity<RestResponse<String>> vnpayHandleIpn(HttpServletRequest request) {
        RestResponse<String> response = paymentService.handleVnPayIpn(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
