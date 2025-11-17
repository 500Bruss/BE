package com.insurance.ktmp.dto.request;

import com.insurance.ktmp.enums.AddOnsStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddonsCreationRequest {

    Long productId;
    String code;
    String name;
    String description;
    BigDecimal price;
    AddOnsStatus status;
    String metaData;
}
