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
    String id;
    String quoteId;
    String productId;
    String productName;
    String userId;
    String userName;
    String applicantData;
    String insuredData;
    String status;
    BigDecimal totalPremium;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
