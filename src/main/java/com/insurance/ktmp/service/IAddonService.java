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

    RestResponse<AddonsResponse> updateAddon(Long id, AddonsUpdateRequest request);

    RestResponse<AddonsResponse> getAddonById(Long id);

    RestResponse<List<AddonsResponse>> getAllAddons();

    RestResponse<List<AddonsResponse>> getAddonsByProduct(Long productId);

    RestResponse<Void> markAsInactive(Long id);

    RestResponse<Void> markAsActive(Long id);

    RestResponse<String> updateAddonVisible(Long addonId, Long userId, String status);
    RestResponse<ListResponse<AddonsResponse>> getAddonListByFilter(
            int page, int size, String sort, String filter, String search, boolean all
    );


}
