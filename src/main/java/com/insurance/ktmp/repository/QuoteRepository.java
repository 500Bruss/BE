package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.Quote;
import com.insurance.ktmp.enums.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Optional<Quote> findByIdAndUser_IdAndStatus(Long quoteId, Long userId, QuoteStatus quoteStatus);
    Optional<Quote> findByUser_IdAndStatus(Long userId, QuoteStatus quoteStatus);
}
