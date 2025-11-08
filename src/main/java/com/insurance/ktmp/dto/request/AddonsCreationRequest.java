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

public class AddonsCreationRequest {
    String code;
    String name;
    String description;
    BigDecimal price;
    Boolean active;
    String metaData;
}
