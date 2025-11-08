package com.insurance.ktmp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AddonsUpdateRequest {
    String name;
    String description;
    BigDecimal price;
    Boolean active;
    String metaData;

}
