package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.dto.response.PaymentResponse;
import com.insurance.ktmp.entity.Claim;
import com.insurance.ktmp.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

    @Mapping(target = "policyId", source = "policy.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    ClaimResponse toClaimResponse(Claim claim);
}
