package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.PredefinedRole;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.PolicyResponse;
import com.insurance.ktmp.entity.*;
import com.insurance.ktmp.enums.PolicyStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.PolicyMapper;
import com.insurance.ktmp.repository.*;
import com.insurance.ktmp.service.IPolicyService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements IPolicyService {

    // SỬA LỖI: Thêm 'final' để @RequiredArgsConstructor (Lombok) tạo Constructor Injection
    private final PolicyRepository policyRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PolicyHistoryRepository policyHistoryRepository;
    private final PolicyMapper policyMapper; // Đảm bảo PolicyMapper là @Component/Mapper

    private static final String POLICY_PREFIX = "PL-";
    private static final List<String> SEARCH_FIELDS = List.of("status");

    /**
     * Helper method để kiểm tra quyền Admin
     */
    private void checkAdminRole(User user) {
        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }
    }


    @Override
    public RestResponse<PolicyResponse> createPolicy(Long applicationId, Long userId) {

        // 1. Check user và quyền Admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        checkAdminRole(user);

        // 2. Lấy application
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        // 3. Tạo policy DRAFT
        Policy policy = new Policy();
        policy.setId(IdGenerator.generateRandomId());
        policy.setPolicyNumber(POLICY_PREFIX + System.currentTimeMillis()); // Sử dụng PREFIX
        policy.setApplication(app);
        policy.setUser(app.getUser());
        policy.setProduct(app.getProduct());
        policy.setPolicyData(app.getApplicantData());
        policy.setStartDate(LocalDateTime.now());
        policy.setEndDate(LocalDateTime.now().plusYears(1));
        policy.setPremiumTotal(app.getTotalPremium());

        // Cải tiến: Sử dụng Enum trực tiếp nếu Policy Entity được cấu hình đúng
        policy.setStatus(PolicyStatus.DRAFT.name());

        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());
        policyRepository.save(policy);

        // 4. Ghi history
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
        // Dòng này đã được sửa lỗi NullPointerException nhờ việc thêm 'final'
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

        // 1. Check user và quyền ADMIN
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        checkAdminRole(user); // Tái sử dụng hàm kiểm tra quyền

        // 2. Parse enum
        PolicyStatus statusEnum;
        try {
            statusEnum = PolicyStatus.valueOf(newStatus.toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE);
        }

        // 3. Lấy policy
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        String oldStatus = policy.getStatus();

        // 4. Update
        policy.setStatus(statusEnum.name());
        policy.setUpdatedAt(LocalDateTime.now());
        policyRepository.save(policy);

        // 5. Ghi history
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
    @Override
    public RestResponse<ListResponse<PolicyResponse>> getPolicyListByFilter(
            int page, int size, String sort, String filter, String search, boolean all
    ) {
        Specification<Policy> sortable = RSQLJPASupport.toSort(sort);
        Specification<Policy> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<Policy> searchable = SearchHelper.parseSearchToken(search, SEARCH_FIELDS);

        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);

        Page<PolicyResponse> responses = policyRepository
                .findAll(sortable.and(filterable).and(searchable), pageable)
                .map(policyMapper::toPolicyResponse);

        return RestResponse.ok(ListResponse.of(responses));
    }

}