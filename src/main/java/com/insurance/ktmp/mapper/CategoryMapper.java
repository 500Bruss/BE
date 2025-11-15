package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.CategoryResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Category;
import com.insurance.ktmp.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {


    CategoryResponse toCategoryResponse(Category request);

}
