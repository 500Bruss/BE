package com.insurance.ktmp.repository;
import java.util.List;
import com.insurance.ktmp.entity.Addon;
import com.insurance.ktmp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddonRepository extends JpaRepository<Addon, Long>, JpaSpecificationExecutor<Addon> {
    boolean existsByCode(String code);

    List<Addon> findByProductId(Long productId);
}
