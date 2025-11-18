package com.insurance.ktmp.mapper;

import com.insurance.ktmp.dto.response.QuoteResponse;
import com.insurance.ktmp.entity.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    QuoteResponse toQuoteResponse(Quote quote);
}
