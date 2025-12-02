package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ClaimCreationRequest;
import com.insurance.ktmp.dto.request.ClaimReviewRequest;
import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.dto.response.ListResponse;

public interface IClaimService {
    RestResponse<ClaimResponse> createClaim(Long userId, Long policy, ClaimCreationRequest request);

    RestResponse<ListResponse<ClaimResponse>> getAllClaimsByFilter(int page, int size, String sort, String filter, String search, boolean all);

    RestResponse<ClaimResponse> reviewClaim(Long userId, Long claimId, ClaimReviewRequest request);
}
