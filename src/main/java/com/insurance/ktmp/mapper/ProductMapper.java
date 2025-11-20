package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.request.ProductUpdateRequest;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AddonMapper.class})
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "addons", target = "addonsList")
    ProductResponse toProductResponse(Product product);

    Product toProduct(ProductCreationRequest request);

    void updateProductFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}

