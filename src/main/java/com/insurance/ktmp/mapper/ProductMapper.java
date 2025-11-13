package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductCreationRequest request);

    ProductResponse toProductResponse(Product product);

    AddonsResponse toAddonsResponse(AddonsResponse response);
}
