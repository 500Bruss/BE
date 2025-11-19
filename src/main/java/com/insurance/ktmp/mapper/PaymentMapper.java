package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.response.ApplicationResponse;
import com.insurance.ktmp.dto.response.PaymentResponse;
import com.insurance.ktmp.entity.Application;
import com.insurance.ktmp.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toPaymentResponse(Payment payment);
}
