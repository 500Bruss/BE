package com.insurance.ktmp.service;

import com.insurance.ktmp.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(Product p);
    Product update(Long id, Product p);
    void delete(Long id);
    Product getById(Long id);
    List<Product> listAll();
}
