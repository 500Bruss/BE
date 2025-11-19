package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PolicyRepository extends
        JpaRepository<Policy, Long>,
        JpaSpecificationExecutor<Policy> {

}
