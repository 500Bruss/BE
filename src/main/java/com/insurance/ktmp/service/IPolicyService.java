package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.PolicyCreationRequest;
import com.insurance.ktmp.dto.response.PolicyResponse;
import com.insurance.ktmp.dto.response.ListResponse;

public interface IPolicyService {

    RestResponse<PolicyResponse> createPolicy(Long applicationId, Long userId);

    RestResponse<String> expirePolicy(Long policyId);

    RestResponse<String> updatePolicyStatus(Long id, String status, Long userId);
    RestResponse<ListResponse<PolicyResponse>> getPolicyListByFilter(
            int page,
            int size,
            String sort,
            String filter,
            String search,
            boolean all
    );
}
