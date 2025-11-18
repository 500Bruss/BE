package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
}
