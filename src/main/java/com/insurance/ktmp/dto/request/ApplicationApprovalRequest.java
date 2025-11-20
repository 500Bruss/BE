package com.insurance.ktmp.dto.request;


import com.insurance.ktmp.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationApprovalRequest {
    private ApplicationStatus status;   // APPROVED hoặc REJECTED
    private String note;                // Optional: lý do reject hoặc ghi chú duyệt
}

