package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Addon;
import com.insurance.ktmp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddonRepository extends JpaRepository<Addon, Long>, JpaSpecificationExecutor<Addon> {
}
