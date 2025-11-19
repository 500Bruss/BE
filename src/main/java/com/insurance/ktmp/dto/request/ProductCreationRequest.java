package com.insurance.ktmp.dto.request;

import com.insurance.ktmp.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {
    @NonNull
    String name;
    String description;

    @NonNull
    Long categoryId;

    @NonNull
    BigDecimal price;
    String baseCover;
    String metaData;

    List<AddonsCreationRequest> listAddOns;
}