package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.request.ProductUpdateRequest;
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

import static org.reflections.Reflections.log;


@RestController
@RequestMapping("/api/products")

@RequiredArgsConstructor
public class ProductController extends BaseController{
    private final IProductService productService;

    @GetMapping
    public ResponseEntity<RestResponse<ListResponse<ProductResponse>>> getListProductsByFilter(
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
    @DeleteMapping("/{productId}")
    public ResponseEntity<RestResponse<Void>> deleteProduct(
            @PathVariable Long productId,
            HttpServletRequest httpReq
    ) {
        // 1. Lấy userId từ JWT (BaseController bạn đã có hàm này)
        Long userId = extractUserIdFromRequest(httpReq);
        log.info("USER ID = " + userId);

        // 2. Gọi service
        RestResponse<Void> response = productService.deleteProduct(productId, userId);

        // 3. Trả về 200 OK (hoặc 204 nếu bạn muốn)
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<ProductResponse>> getById(
            @PathVariable Long id) {

        RestResponse<ProductResponse> response = productService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ================== UPDATE ==================
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductUpdateRequest request) {

        RestResponse<ProductResponse> response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
}
