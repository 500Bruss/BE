package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.request.ApplicationCreationRequest;
import com.insurance.ktmp.dto.response.ApplicationResponse;
import com.insurance.ktmp.entity.Application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ApplicationMapper {

    @Mapping(target = "quoteId", source = "quote.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "userId", source = "user.id")
    // map enum -> String
    @Mapping(target = "status", expression = "java(application.getStatus().name())")

    // map totalPremium
    @Mapping(target = "totalPremium", source = "totalPremium")
    ApplicationResponse toResponse(Application application);
}
