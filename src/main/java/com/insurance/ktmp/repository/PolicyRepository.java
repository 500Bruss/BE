package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long>, JpaSpecificationExecutor<Policy> {
    boolean existsByPolicyNumber(String policyNumber);

    Optional<Policy> findByApplicationId(Long applicationId);
}
