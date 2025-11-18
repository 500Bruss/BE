package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ClaimCreationRequest;
import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.entity.Application;

public interface IClaimService {
    RestResponse<ClaimResponse> createClaim(Long userId, Long policy, ClaimCreationRequest request);
}
