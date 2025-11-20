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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        checkAdminRole(user);

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        Policy policy = new Policy();
        policy.setId(IdGenerator.generateRandomId());
        policy.setPolicyNumber(POLICY_PREFIX + System.currentTimeMillis()); // Sử dụng PREFIX
        policy.setApplication(app);
        policy.setUser(app.getUser());
        policy.setProduct(app.getProduct());
        policy.setPolicyData(app.getApplicantData());
        policy.setStartDate(LocalDateTime.now());
        policy.setEndDate(LocalDateTime.now().plusMinutes(5)); //todo: plusYear
        policy.setPremiumTotal(app.getTotalPremium());

        // Cải tiến: Sử dụng Enum trực tiếp nếu Policy Entity được cấu hình đúng
        policy.setStatus(PolicyStatus.ACTIVE.name());

        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());
        policyRepository.save(policy);

        createPolicyHistory(policy, null, PolicyStatus.ACTIVE.name(), "Policy created in ACTIVE");

        return RestResponse.ok(policyMapper.toPolicyResponse(policy));
    }

    private void createPolicyHistory(Policy policy, String oldStatus, String newStatus, String note) {
        PolicyHistory policyHistory = PolicyHistory.builder()
                .id(IdGenerator.generateRandomId())
                .policy(policy)
                .changedBy(policy.getUser())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedAt(LocalDateTime.now())
                .note(note)
                .build();
        policyHistoryRepository.save(policyHistory);
    }

    @Override
    @Scheduled(cron = "0 */7 * * * ?") // Chạy mỗi 7 phút
    public RestResponse<String> expirePolicy() {
        List<Policy> policies = policyRepository
                .findAllByEndDateBeforeAndStatus(LocalDateTime.now(), PolicyStatus.ACTIVE.name());
        policies.forEach(policy -> {
            updatePolicyStatus(policy.getId(), PolicyStatus.EXPIRED.name(), 898454043L, true);
        });
        return RestResponse.ok("Complete update policy status to expired");
    }

    @Override
    @Transactional
    public RestResponse<String> updatePolicyStatus(Long policyId, String newStatus,
                                                   Long userId, boolean isCronJob) {

        if (!isCronJob) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            checkAdminRole(user); // Tái sử dụng hàm kiểm tra quyền
        }

        PolicyStatus statusEnum;
        try {
            statusEnum = PolicyStatus.valueOf(newStatus.toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE);
        }

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        String oldStatus = policy.getStatus();

        policy.setStatus(statusEnum.name());
        policy.setUpdatedAt(LocalDateTime.now());
        policyRepository.save(policy);

        createPolicyHistory(policy, oldStatus, statusEnum.name()
                , "Status updated to " + statusEnum.name());

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