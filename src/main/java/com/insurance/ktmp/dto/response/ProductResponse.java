package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String name;
    String description;
    String categoryId;
    String categoryName;
    BigDecimal price;
    String baseCover;
    ProductStatus status;
    Boolean visible;
    String metaData;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<AddonsResponse> addonsList;
}
