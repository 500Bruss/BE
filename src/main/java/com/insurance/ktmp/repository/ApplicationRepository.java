package com.insurance.ktmp.repository;


import com.insurance.ktmp.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ApplicationRepository extends
        JpaRepository<Application, Long>,
        JpaSpecificationExecutor<Application> {
}

