package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.dto.request.ProductUpdateRequest;

import com.insurance.ktmp.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductCreationRequest request);

    ProductResponse toProductResponse(Product product);

    AddonsResponse toAddonsResponse(AddonsResponse response);

    void updateProductFromRequest(ProductUpdateRequest request, @MappingTarget Product product);

}
