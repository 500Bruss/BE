package com.insurance.ktmp.repository;

import com.insurance.ktmp.entity.QuoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteItemRepository extends JpaRepository<QuoteItem, Long> {


    List<QuoteItem> findByQuote_Id(Long quoteId);
}
