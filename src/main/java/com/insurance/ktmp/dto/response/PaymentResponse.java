package com.insurance.ktmp.dto.response;

import com.insurance.ktmp.enums.PaymentMethod;
import com.insurance.ktmp.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    Long applicationId;
    Long quoteId;
    Long userId;
    String userName;
    PaymentMethod paymentMethod;
    BigDecimal amount;
    String providerReference;
    PaymentStatus status;
    String vnpayResp;
    LocalDateTime transactionTime;
    String transactionId;
    String paymentUrl;
}
