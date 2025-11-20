package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.entity.Addon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddonMapper {

    @Mapping(source = "product.id", target = "productId")
    AddonsResponse toAddonsResponse(Addon addon);

    List<AddonsResponse> toAddonsResponses(List<Addon> addons);
}

