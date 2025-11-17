package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ApplicationCreationRequest;
import com.insurance.ktmp.dto.response.ApplicationResponse;

public interface IApplicationService {
    RestResponse<ApplicationResponse> createApplication(Long userId, ApplicationCreationRequest request);
    RestResponse<ApplicationResponse> getById(Long id);
}

