package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.service.IProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController{
    private final IProductService productService;

    @GetMapping
    public ResponseEntity<RestResponse<ListResponse<ProductResponse>>> getListDrones(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) boolean all) {
        RestResponse<ListResponse<ProductResponse>> list = productService.getListProductsByFilter(page, size, sort, filter, search, all);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<RestResponse<ProductResponse>> createProduct(
            @RequestBody @Valid ProductCreationRequest request,
            HttpServletRequest htpReq) {
        Long userId =extractUserIdFromRequest(htpReq);
        RestResponse<ProductResponse> response = productService.createProduct(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
