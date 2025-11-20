package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.request.ApplicationStatusUpdateRequest;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.dto.request.ApplicationCreationRequest;
import com.insurance.ktmp.dto.response.ApplicationResponse;
import com.insurance.ktmp.service.IApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController extends BaseController {

    private final IApplicationService service;

    @PostMapping("/{quoteId}")
    public ResponseEntity<RestResponse<ApplicationResponse>> createApplication(
            HttpServletRequest req,
            @PathVariable Long quoteId,
            @RequestBody ApplicationCreationRequest request
    ) {
        Long userId = extractUserIdFromRequest(req);
        return ResponseEntity.ok(service.createApplication(userId, quoteId, request));
    }

    @GetMapping
    public ResponseEntity<RestResponse<ListResponse<ApplicationResponse>>> getListApplicationByFilter(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) boolean all
    ) {
        RestResponse<ListResponse<ApplicationResponse>> list =
                service.getListApplicationByFilter(page, size, sort, filter, search, all);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<ApplicationResponse>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RestResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(service.updateStatus(id, request.getStatus()));
    }



}

