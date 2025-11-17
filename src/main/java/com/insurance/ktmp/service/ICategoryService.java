package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.CategoryCreationRequest;
import com.insurance.ktmp.dto.request.CategoryUpdateRequest;
import com.insurance.ktmp.dto.response.CategoryResponse;
import com.insurance.ktmp.dto.response.ListResponse;

import java.util.List;

public interface ICategoryService {

    RestResponse<CategoryResponse> createCategory(CategoryCreationRequest request);

    RestResponse<CategoryResponse> updateCategory(Long id, CategoryUpdateRequest request);

    RestResponse<CategoryResponse> getCategoryById(Long id);

    RestResponse<List<CategoryResponse>> getAllCategories();


    RestResponse<Void> markAsInactive(Long id);

    RestResponse<Void> markAsActive(Long id);

    RestResponse<String> updateCategoryVisible(Long id, Long userId, String status);

    RestResponse<ListResponse<CategoryResponse>> getCategoryListByFilter(
            int page,
            int size,
            String sort,
            String filter,
            String search,
            boolean all
    );
}
