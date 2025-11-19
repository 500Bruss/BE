package com.insurance.ktmp.dto.request;

import com.insurance.ktmp.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCreationRequest {
    Long applicationId;
    PaymentMethod method;
}
