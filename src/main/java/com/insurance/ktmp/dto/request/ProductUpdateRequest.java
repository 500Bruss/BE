package com.insurance.ktmp.dto.request;

import com.insurance.ktmp.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {

        String name;
        String description;
        Long category;
        BigDecimal price;
        String baseCover;
        ProductStatus status;
        Boolean visible;
        String metadata;

}
