package com.insurance.ktmp.dto.request;

import com.insurance.ktmp.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    String name;
    String description;

    @NotNull
    Long categoryId;

    @NotNull
    BigDecimal price;
    String baseCover;
    String metaData;

    List<AddonsCreationRequest> listAddOns;
}