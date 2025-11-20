package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PolicyRepository extends
        JpaRepository<Policy, Long>,
        JpaSpecificationExecutor<Policy> {
    List<Policy> findAllByEndDateBeforeAndStatus(LocalDateTime currentDate, String status);
}
