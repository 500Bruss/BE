package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.entity.Addon;
import com.insurance.ktmp.enums.AddOnsStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AddonMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "status", source =  "status")
    AddonsResponse toResponse(Addon addon);

    List<AddonsResponse> toResponseList(List<Addon> addons);


}
