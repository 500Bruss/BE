package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.CategoryStatus;
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
    String id;
    String code;
    String name;
    String description;
    CategoryStatus status;
    String metaData;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    Long createBy;
    Long updateBy;
}
