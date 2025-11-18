package com.insurance.ktmp.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;
import com.insurance.ktmp.enums.ApplicationStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ApplicationStatusUpdateRequest {

        private ApplicationStatus status;

}
