package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.AddonsCreationRequest;
import com.insurance.ktmp.dto.request.AddonsUpdateRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.enums.AddOnsStatus;
import com.insurance.ktmp.service.IAddonService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addons")
@RequiredArgsConstructor
public class AddonController extends BaseController {

    private final IAddonService addonService;

    @PostMapping("/{productId}")
    public ResponseEntity<RestResponse<ProductResponse>> createAddon(
            @PathVariable Long productId,
            @RequestBody AddonsCreationRequest request,
            HttpServletRequest httpReq
    ) {
        Long userId = extractUserIdFromRequest(httpReq);
        return ResponseEntity.ok(addonService.createAddon(userId, productId, request));
    }

    // ========================= UPDATE =========================
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<ProductResponse>> updateAddon(
            @PathVariable Long id,
            @RequestBody AddonsUpdateRequest request,
            HttpServletRequest httpReq
    ) {
        Long userId = extractUserIdFromRequest(httpReq);
        return ResponseEntity.ok(addonService.updateAddon(userId, id, request));
    }

    // ========================= UPDATE STATUS (có user check) ================
    @GetMapping("/{addonId}/status/{status}")
    public ResponseEntity<RestResponse<String>> updateAddonStatus(
            @PathVariable Long addonId,
            @PathVariable String status,
            HttpServletRequest request
    ) {
        Long userId = extractUserIdFromRequest(request);
        RestResponse<String> response = addonService.updateAddonVisible(addonId, userId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public RestResponse<ListResponse<AddonsResponse>> getByProduct(
            @RequestParam Long productId
    ) {
        return RestResponse.ok(addonService.getByProduct(productId));
    }




}
