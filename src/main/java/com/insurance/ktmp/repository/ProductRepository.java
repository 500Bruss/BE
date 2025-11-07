package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCode(String code);

    List<Product> findByActiveTrue();
}
