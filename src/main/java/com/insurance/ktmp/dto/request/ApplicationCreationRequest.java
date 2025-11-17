package com.insurance.ktmp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationCreationRequest {
    Long quoteId;
    Long productId;
    String applicantData;  // JSON string
    String insuredData;    // JSON string
    BigDecimal totalPremium;
}

