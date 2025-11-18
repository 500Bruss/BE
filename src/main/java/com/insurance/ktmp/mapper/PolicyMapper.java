package com.insurance.ktmp.mapper;

import com.insurance.ktmp.entity.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PolicyMapper {
/*
    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")
    PolicyResponse toPolicyResponse(Policy policy);*/
}
