package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.AddOnsStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddonsResponse {
    String id;
    String productId;
    String code;
    String name;
    String description;
    BigDecimal price;
    AddOnsStatus status;
    String metaData;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
