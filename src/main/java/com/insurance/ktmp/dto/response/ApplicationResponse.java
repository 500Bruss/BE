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
public class ApplicationResponse {
    Long id;
    Long quoteId;
    Long productId;
    Long userId;
    String applicantData;
    String insuredData;
    String status;
    BigDecimal totalPremium;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
