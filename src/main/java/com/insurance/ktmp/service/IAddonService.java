package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.AddonsCreationRequest;
import com.insurance.ktmp.dto.request.AddonsUpdateRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;

import java.util.List;

public interface IAddonService {

    RestResponse<ProductResponse> createAddon(Long userId, Long productId, AddonsCreationRequest request);

    RestResponse<ProductResponse> updateAddon(Long userId, Long id, AddonsUpdateRequest request);

    RestResponse<String> updateAddonVisible(Long addonId, Long userId, String status);

    // NEW: user xem addons theo product
    ListResponse<AddonsResponse> getByProduct(Long productId);


}
