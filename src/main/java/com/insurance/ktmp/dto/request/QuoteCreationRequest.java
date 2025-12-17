package com.insurance.ktmp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuoteCreationRequest {
    @NotNull
    Long productId;
    String inputData;
    List<Long> selectedAddons;
}
