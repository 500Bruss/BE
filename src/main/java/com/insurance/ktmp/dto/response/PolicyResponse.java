package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.PolicyStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {

    Long id;
    String policyNumber;

    Long applicationId;
    Long userId;
    Long productId;

    String policyData;

    LocalDateTime startDate;
    LocalDateTime endDate;

    PolicyStatus status;

    BigDecimal premiumTotal;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

