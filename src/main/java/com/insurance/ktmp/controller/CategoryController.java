package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.CategoryCreationRequest;
import com.insurance.ktmp.dto.request.CategoryUpdateRequest;
import com.insurance.ktmp.dto.response.CategoryResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.enums.CategoryStatus;
import com.insurance.ktmp.service.ICategoryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController extends BaseController {

    private final ICategoryService categoryService;

    // ========================= CREATE =========================
    @PostMapping
    public ResponseEntity<RestResponse<CategoryResponse>> createCategory(
            @RequestBody CategoryCreationRequest request
    ) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    // ========================= UPDATE =========================
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryUpdateRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    // ========================= GET BY ID ========================
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<RestResponse<ListResponse<CategoryResponse>>> getCategoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) boolean all
    ) {
        return ResponseEntity.ok(
                categoryService.getCategoryListByFilter(page, size, sort, filter, search, all)
        );
    }
    // ========================= INACTIVE =========================
    @PutMapping("/{id}/inactive")
    public ResponseEntity<RestResponse<String>> markAsInactive(@PathVariable Long id) {
        categoryService.markAsInactive(id);
        return ResponseEntity.ok(RestResponse.ok("Category marked as INACTIVE"));
    }

    // ========================= ACTIVE =========================
    @PutMapping("/{id}/active")
    public ResponseEntity<RestResponse<String>> markAsActive(@PathVariable Long id) {
        categoryService.markAsActive(id);
        return ResponseEntity.ok(RestResponse.ok("Category marked as ACTIVE"));
    }

    @GetMapping("/{categoryId}/status/{status}")
    public ResponseEntity<RestResponse<String>> updateCategoryStatus(
            @PathVariable Long categoryId,
             @PathVariable String status,
            HttpServletRequest request
    ) {
        Long userId = extractUserIdFromRequest(request);
        RestResponse<String> response = categoryService.updateCategoryVisible(categoryId, userId, status);
        return ResponseEntity.ok(response);
    }
}
