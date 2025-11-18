package com.insurance.ktmp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ClaimCreationRequest;
import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.entity.Claim;
import com.insurance.ktmp.entity.Policy;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.ClaimStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.ClaimMapper;
import com.insurance.ktmp.repository.ClaimRepository;
import com.insurance.ktmp.repository.PolicyRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.IClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements IClaimService {
    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ObjectMapper objectMapper;
    private final ClaimMapper claimMapper;

    @Override
    public RestResponse<ClaimResponse> createClaim(Long userId, Long policyId, ClaimCreationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new AppException(ErrorCode.POLICY_NOT_FOUND));

        if (!policy.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        String claimData;
        try {
            claimData = objectMapper.writeValueAsString(request.getClaimData());
        } catch (Exception e) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Claim claim = Claim.builder()
                .id(IdGenerator.generateRandomId())
                .policy(policy)
                .user(user)
                .incidentDate(request.getIncidentDate())
                .reportedAt(LocalDateTime.now())
                .claimData(claimData)
                .amountClaimed(request.getAmountClaimed())
                .status(ClaimStatus.SUBMITTED.name())
                .resolutionNote(request.getResolutionNote())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return RestResponse.ok(claimMapper.toClaimResponse(claim));
    }
}
