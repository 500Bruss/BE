package com.insurance.ktmp.controller;

import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController{
    private final ProductService productService;

}
