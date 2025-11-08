package com.insurance.ktmp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AddonsReponse {
    Long id;
    Long productId;
    String code;
    String name;
    String description;
    BigDecimal price;
    Boolean active;
    String metaData;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
