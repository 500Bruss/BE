package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Long id;
    String code;
    String name;
    String description;
    ProductStatus productStatus;
    String metaData;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    Long createBy;
    Long updateBy;
}
