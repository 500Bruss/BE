package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;

public interface IProductService {
    RestResponse<ListResponse<ProductResponse>> getListProductsByFilter(int page, int size, String sort, String filter, String search, boolean all);

    RestResponse<ProductResponse> createProduct(Long userId, ProductCreationRequest request);
}
