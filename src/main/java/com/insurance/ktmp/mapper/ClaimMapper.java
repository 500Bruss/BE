package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.dto.response.PaymentResponse;
import com.insurance.ktmp.entity.Claim;
import com.insurance.ktmp.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

    ClaimResponse toClaimResponse(Claim claim);
}
