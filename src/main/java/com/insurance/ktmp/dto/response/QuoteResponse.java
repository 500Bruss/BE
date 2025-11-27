package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.QuoteStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuoteResponse {
    String id;
    String userId;
    String userName;
    String productId;
    String productName;
    String inputData;
    BigDecimal premium;
    QuoteStatus status;
    LocalDateTime validUntil;
}
