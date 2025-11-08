package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.entity.Category;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.exception.ApiException;
import com.insurance.ktmp.exception.NotFoundException;
import com.insurance.ktmp.repository.CategoryRepository;
import com.insurance.ktmp.repository.ProductRepository;
import com.insurance.ktmp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

//    private final ProductRepository productRepo;
//    private final CategoryRepository categoryRepo;
//
//    @Override
//    public Product create(Product p) {
//        log.info("Creating new product: {}", p.getName());
//
//        if (p.getCategory() == null || p.getCategory().getId() == null)
//            throw new ApiException("Category is required");
//
//        Category cat = categoryRepo.findById(p.getCategory().getId())
//                .orElseThrow(() -> new NotFoundException("Category not found"));
//
//        if (productRepo.existsByCode(p.getCode()))
//            throw new ApiException("Product code already exists");
//
//        p.setCategory(cat);
//        p.setActive(true);
//        return productRepo.save(p);
//    }
//
//    @Override
//    public Product update(Long id, Product p) {
//        Product ex = productRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Product not found"));
//
//        if (p.getName() != null) ex.setName(p.getName());
//        if (p.getDescription() != null) ex.setDescription(p.getDescription());
//        if (p.getBasePrice() != null) ex.setBasePrice(p.getBasePrice());
//        if (p.getActive() != null) ex.setActive(p.getActive());
//
//        // Update category nếu có truyền vào
//        if (p.getCategory() != null && p.getCategory().getId() != null) {
//            Category cat = categoryRepo.findById(p.getCategory().getId())
//                    .orElseThrow(() -> new NotFoundException("Category not found"));
//            ex.setCategory(cat);
//        }
//
//        log.info("Updated product: {}", ex.getId());
//        return productRepo.save(ex);
//    }
//
//    @Override
//    public void delete(Long id) {
//        Product p = productRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Product not found"));
//        // (Optionally: check liên kết policies/quotations)
//
//        log.warn("Deleting product id={}", id);
//        productRepo.delete(p);
//    }
//
//    @Override
//    public Product getById(Long id) {
//        return productRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Product not found"));
//    }
//
//    @Override
//    public List<Product> listAll() {
//        return productRepo.findByActiveTrue();
//    }
}
