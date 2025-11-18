package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
