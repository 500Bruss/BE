package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductResponse {
    BigInteger id;
    String name;
    String description;
    BigDecimal price;
    String baseCover;
    ProductStatus status;
    Boolean visible;
    String metaData;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long createdBy;
    Long updatedBy;
}
