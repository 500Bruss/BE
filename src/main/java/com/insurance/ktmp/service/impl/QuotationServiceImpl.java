package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.exception.ApiException;
import com.insurance.ktmp.exception.NotFoundException;
import com.insurance.ktmp.repository.ProductRepository;
import com.insurance.ktmp.repository.QuotationRepository;
import com.insurance.ktmp.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {
    private static final Logger log = LoggerFactory.getLogger(QuotationServiceImpl.class);

    private final QuotationRepository quotationRepo;
    private final ProductRepository productRepo;

    @Override
    public Quotation create(Quotation q) {
        log.info("Creating quotation for product={}, customer={}",
                q.getProduct() != null ? q.getProduct().getId() : null, q.getCustomerId());

        if (q.getProduct() == null || q.getProduct().getId() == null)
            throw new ApiException("Product is required");

        Product product = productRepo.findById(q.getProduct().getId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Kiểm tra khách hàng hợp lệ (demo)
        if (q.getCustomerId() == null)
            throw new ApiException("Customer ID is required");

        // Demo công thức tính phí tạm tính
        double base = product.getBasePrice() != null ? product.getBasePrice() : 0.0;
        double riskFactor = generateRiskFactor(q.getInputDataJson());
        double calculated = base * (1 + riskFactor);

        q.setProduct(product);
        q.setCalculatedPremium(calculated);
        q.setStatus("DRAFT");
        q.setCreatedAt(Instant.now());

        log.info("Quotation created with premium={}", calculated);
        return quotationRepo.save(q);
    }

    @Override
    public Quotation submit(Long id) {
        Quotation q = quotationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Quotation not found"));

        if (!"DRAFT".equals(q.getStatus()))
            throw new ApiException("Only DRAFT quotations can be submitted");

        q.setStatus("SUBMITTED");
        return quotationRepo.save(q);
    }

    @Override
    public Quotation getById(Long id) {
        return quotationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Quotation not found"));
    }

    @Override
    public List<Quotation> listAll() {
        return quotationRepo.findAll();
    }

    @Override
    public List<Quotation> listByCustomer(Long customerId) {
        return quotationRepo.findByCustomerId(customerId);
    }

    // ====== PRIVATE HELPER ======
    private double generateRiskFactor(String inputJson) {
        // (Giả lập đọc thông tin khách hàng: tuổi, giới tính, nghề,...)
        // Ở đây mock random nhỏ
        return 0.05 + new Random().nextDouble() * 0.15; // từ 5% đến 20%
    }
}
