package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.AddonsCreationRequest;
import com.insurance.ktmp.dto.request.AddonsUpdateRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ListResponse;
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

    // ========================= CREATE =========================
    @PostMapping
    public ResponseEntity<RestResponse<AddonsResponse>> createAddon(
            @RequestBody AddonsCreationRequest request
    ) {
        return ResponseEntity.ok(addonService.createAddon(request));
    }

    // ========================= UPDATE =========================
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<AddonsResponse>> updateAddon(
            @PathVariable Long id,
            @RequestBody AddonsUpdateRequest request
    ) {
        return ResponseEntity.ok(addonService.updateAddon(id, request));
    }

    // ========================= GET BY ID =========================
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<AddonsResponse>> getAddonById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(addonService.getAddonById(id));
    }

    // ========================= FILTER LIST ========================
    @GetMapping
    public ResponseEntity<RestResponse<ListResponse<AddonsResponse>>> getAddonList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) boolean all
    ) {
        return ResponseEntity.ok(
                addonService.getAddonListByFilter(page, size, sort, filter, search, all)
        );
    }

    // ========================= INACTIVE =========================
    @PutMapping("/{id}/inactive")
    public ResponseEntity<RestResponse<String>> markAsInactive(@PathVariable Long id) {
        addonService.markAsInactive(id);
        return ResponseEntity.ok(RestResponse.ok("Addon marked as INACTIVE"));
    }

    // ========================= ACTIVE =========================
    @PutMapping("/{id}/active")
    public ResponseEntity<RestResponse<String>> markAsActive(@PathVariable Long id) {
        addonService.markAsActive(id);
        return ResponseEntity.ok(RestResponse.ok("Addon marked as ACTIVE"));
    }

    // ========================= UPDATE STATUS (có user check) ================
    @PutMapping("/{addonId}/status/{status}")
    public ResponseEntity<RestResponse<String>> updateAddonStatus(
            @PathVariable Long addonId,
            @PathVariable String status,
            HttpServletRequest request
    ) {
        Long userId = extractUserIdFromRequest(request);

        RestResponse<Void> result =
                status.equalsIgnoreCase("ACTIVE")
                        ? addonService.markAsActive(addonId)
                        : addonService.markAsInactive(addonId);

        return ResponseEntity.ok(
                RestResponse.ok("Addon marked as " + status.toUpperCase())
        );
    }
}
