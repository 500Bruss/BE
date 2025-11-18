package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.PredefinedRole;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.PolicyCreationRequest;
import com.insurance.ktmp.dto.response.PolicyResponse;
import com.insurance.ktmp.entity.*;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.PolicyStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.PolicyMapper;
import com.insurance.ktmp.repository.*;
import com.insurance.ktmp.service.IPolicyService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import com.insurance.ktmp.repository.PolicyHistoryRepository;
@Service
@RequiredArgsConstructor

public class PolicyServiceImpl implements IPolicyService {

    PolicyRepository policyRepository;
    ApplicationRepository applicationRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    PolicyHistoryRepository policyHistoryRepository;
    PolicyMapper policyMapper;

    private static final String POLICY_PREFIX = "PL-";
    private static final List<String> SEARCH_FIELDS = List.of("status");


    @Override
    public RestResponse<PolicyResponse> createPolicy(Long applicationId, Long userId) {

        // 1. Check user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Check role admin
        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        // 3. Lấy application
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        // 4. Tạo policy DRAFT
        Policy policy = new Policy();
        policy.setId(IdGenerator.generateRandomId());
        policy.setPolicyNumber("PL-" + System.currentTimeMillis());
        policy.setApplication(app);
        policy.setUser(app.getUser());
        policy.setProduct(app.getProduct());
        policy.setPolicyData(app.getApplicantData());
        policy.setStartDate(LocalDateTime.now());
        policy.setEndDate(LocalDateTime.now().plusYears(1));
        policy.setPremiumTotal(app.getTotalPremium());
        policy.setStatus(PolicyStatus.DRAFT.name());
        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());
        policyRepository.save(policy);

        // 5. history
        PolicyHistory history = new PolicyHistory();
        history.setId(IdGenerator.generateRandomId());
        history.setPolicy(policy);
        history.setChangedBy(user);
        history.setOldStatus(null);
        history.setNewStatus(PolicyStatus.DRAFT.name());
        history.setChangedAt(LocalDateTime.now());
        history.setNote("Policy created in DRAFT");
        policyHistoryRepository.save(history);

        return RestResponse.ok(policyMapper.toPolicyResponse(policy));
    }


    @Override
    public RestResponse<PolicyResponse> getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        return RestResponse.ok(policyMapper.toPolicyResponse(policy));
    }


    @Override
    public RestResponse<String> activatePolicy(Long policyId, Long userId) {
        return updatePolicyStatus(policyId, PolicyStatus.ACTIVE.name(), userId);
    }


    @Override
    public RestResponse<String> expirePolicy(Long policyId, Long userId) {
        return updatePolicyStatus(policyId, PolicyStatus.EXPIRED.name(), userId);
    }


    @Override
    public RestResponse<String> cancelPolicy(Long policyId, Long userId) {
        return updatePolicyStatus(policyId, PolicyStatus.CANCELLED.name(), userId);
    }

    @Override
    public RestResponse<String> updatePolicyStatus(Long policyId, String newStatus, Long userId) {

        // 1. Check user có tồn tại chưa
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Check quyền ADMIN
        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        // 3. Parse enum
        PolicyStatus statusEnum;
        try {
            statusEnum = PolicyStatus.valueOf(newStatus.toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE);
        }

        // 4. Lấy policy
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        String oldStatus = policy.getStatus();

        // 5. Update
        policy.setStatus(statusEnum.name());
        policy.setUpdatedAt(LocalDateTime.now());
        policyRepository.save(policy);

        // 6. Ghi history
        PolicyHistory history = new PolicyHistory();
        history.setId(IdGenerator.generateRandomId());
        history.setPolicy(policy);
        history.setChangedBy(user);
        history.setOldStatus(oldStatus);
        history.setNewStatus(statusEnum.name());
        history.setChangedAt(LocalDateTime.now());
        history.setNote("Status updated to " + statusEnum.name());

        policyHistoryRepository.save(history);

        return RestResponse.ok("Policy updated to " + statusEnum.name());
    }
}

