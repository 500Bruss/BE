package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.PolicyHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyHistoryRepository extends JpaRepository<PolicyHistory, Long> {
    List<PolicyHistory> findByPolicyIdOrderByChangedAtDesc(Long policyId);
}
