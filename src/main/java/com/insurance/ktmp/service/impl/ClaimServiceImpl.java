package com.insurance.ktmp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.PredefinedRole;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.request.ClaimCreationRequest;
import com.insurance.ktmp.dto.request.ClaimReviewRequest;
import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.dto.response.ListResponse;
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
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements IClaimService {
    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ObjectMapper objectMapper;
    private final ClaimMapper claimMapper;

    private static final List<String> SEARCH_FIELDS = List.of("status");

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
                .incidentDate(LocalDate.parse(request.getIncidentDate()).atStartOfDay())
                .reportedAt(LocalDateTime.now())
                .claimData(claimData)
                .amountClaimed(request.getAmountClaimed())
                .status(ClaimStatus.SUBMITTED.name())
                .resolutionNote(request.getResolutionNote())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        claimRepository.save(claim);

        return RestResponse.ok(claimMapper.toClaimResponse(claim));
    }

    @Override
    public RestResponse<ListResponse<ClaimResponse>> getAllClaimsByFilter(
            int page, int size, String sort, String filter, String search, boolean all) {
        Specification<Claim> sortable = RSQLJPASupport.toSort(sort);
        Specification<Claim> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<Claim> searchable = SearchHelper.parseSearchToken(search, SEARCH_FIELDS);
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<ClaimResponse> responses = claimRepository
                .findAll(sortable.and(filterable).and(searchable), pageable)
                .map(claimMapper::toClaimResponse);

        return RestResponse.ok(ListResponse.of(responses));
    }

    @Override
    public RestResponse<ClaimResponse> reviewClaim(Long userId, Long claimId,  ClaimReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAIM_NOT_FOUND));

        if (!claim.getStatus().equals(ClaimStatus.SUBMITTED.name())) {
            throw new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE);
        }

        switch (ClaimStatus.valueOf(request.getStatus())) {
            case APPROVED:
                claim.setStatus(ClaimStatus.APPROVED.name());
                claim.setUpdatedAt(LocalDateTime.now());
                claim.setAssignedOfficer(user);
                break;
            case REJECTED:
                claim.setStatus(ClaimStatus.REJECTED.name());
                claim.setUpdatedAt(LocalDateTime.now());
                claim.setAssignedOfficer(user);
                break;
            case PAID:
                claim.setStatus(ClaimStatus.PAID.name());
                claim.setUpdatedAt(LocalDateTime.now());
                claim.setAssignedOfficer(user);
                break;
            default:
                throw new AppException(ErrorCode.INVALID_BUSINESS_FLOW);
        }
        claimRepository.save(claim);

        return RestResponse.ok(claimMapper.toClaimResponse(claim));
    }
}
