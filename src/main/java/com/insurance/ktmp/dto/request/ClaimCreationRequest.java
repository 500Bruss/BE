package com.insurance.ktmp.dto.request;

import com.insurance.ktmp.enums.ClaimStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaimCreationRequest {
    LocalDateTime incidentDate;
    Map<String, Object> claimData;
    BigDecimal amountClaimed;
    String resolutionNote;
}
