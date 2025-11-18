package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.ClaimStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaimResponse {
    Long id;
    Long policyId;
    Long userId;
    String userName;
    LocalDateTime incidentDate;
    LocalDateTime reportedAt;
    String claimData;
    BigDecimal amountClaimed;
    ClaimStatus status;
}
