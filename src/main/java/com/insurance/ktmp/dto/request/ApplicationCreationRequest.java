package com.insurance.ktmp.dto.request;
import java.util.Map;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationCreationRequest {

    Map<String, Object> applicantData;
    Map<String, Object> insuredData;   // JSON string

}

