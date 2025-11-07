package com.insurance.ktmp.service;

import com.insurance.ktmp.entity.Quotation;

import java.util.List;

public interface QuotationService {
    Quotation create(Quotation quotation);
    Quotation submit(Long id);
    Quotation getById(Long id);
    List<Quotation> listAll(); // Admin xem tất cả
    List<Quotation> listByCustomer(Long customerId);
}
