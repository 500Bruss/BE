package com.insurance.ktmp.controller;

import com.insurance.ktmp.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController {
    private final QuotationService quotationService;

    // 🧍 Khách hàng tạo báo giá
    @PostMapping
    public ResponseEntity<Quotation> create(@RequestBody Quotation quotation) {
        Quotation q = quotationService.create(quotation);
        return ResponseEntity.status(HttpStatus.CREATED).body(q);
    }

    // ✅ Khách hàng xác nhận gửi báo giá (submit)
    @PutMapping("/{id}/submit")
    public ResponseEntity<Quotation> submit(@PathVariable Long id) {
        return ResponseEntity.ok(quotationService.submit(id));
    }

    // 🔍 SysAdmin xem tất cả báo giá
    @GetMapping
    public ResponseEntity<List<Quotation>> listAll() {
        return ResponseEntity.ok(quotationService.listAll());
    }

    // 👤 Khách hàng xem báo giá của riêng mình
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Quotation>> listByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(quotationService.listByCustomer(customerId));
    }

    // 🧾 Xem chi tiết báo giá
    @GetMapping("/{id}")
    public ResponseEntity<Quotation> get(@PathVariable Long id) {
        return ResponseEntity.ok(quotationService.getById(id));
    }
}
