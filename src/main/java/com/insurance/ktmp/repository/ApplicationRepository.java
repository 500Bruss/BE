package com.insurance.ktmp.repository;


import com.insurance.ktmp.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.insurance.ktmp.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationRepository extends
        JpaRepository<Application, Long>,
        JpaSpecificationExecutor<Application> {
    List<Application> findByStatusAndCreatedAtBefore(ApplicationStatus status, LocalDateTime time);
}

