package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.addons a
        WHERE p.id = :id
    """)
    Optional<Product> findByIdWithAddons(@Param("id") Long id);
}

