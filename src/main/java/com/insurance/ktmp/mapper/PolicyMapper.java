package com.insurance.ktmp.mapper;

import com.insurance.ktmp.entity.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")


    @Mapping(target = "status",
            expression = "java(com.insurance.ktmp.enums.PolicyStatus.valueOf(policy.getStatus()))")


    PolicyResponse toPolicyResponse(Policy policy);
}

