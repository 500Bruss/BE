package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.enums.ApplicationStatus;
import com.insurance.ktmp.dto.request.ApplicationCreationRequest;
import com.insurance.ktmp.dto.request.ApplicationStatusUpdateRequest;
import com.insurance.ktmp.dto.response.ApplicationResponse;
import com.insurance.ktmp.dto.response.ListResponse;

public interface IApplicationService {

    RestResponse<ListResponse<ApplicationResponse>> getListApplicationByFilter(
            int page,
            int size,
            String sort,
            String filter,
            String search,
            boolean all
    );

    RestResponse<ApplicationResponse> createApplication(
            Long userId,
            Long quoteId,
            ApplicationCreationRequest request
    );
    RestResponse<ApplicationResponse> updateStatus(Long id, ApplicationStatus status);

    RestResponse<ApplicationResponse> getById(Long id);
}
