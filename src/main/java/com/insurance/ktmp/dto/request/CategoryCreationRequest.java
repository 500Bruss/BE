package com.insurance.ktmp.dto.request;

import com.insurance.ktmp.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CategoryCreationRequest {
    String code;
    String name;
    String description;
    ProductStatus status;
    String metaData;
}
