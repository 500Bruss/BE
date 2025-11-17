package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.request.ProductUpdateRequest;
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

    @PostMapping
    public ResponseEntity<RestResponse<ApplicationResponse>> create(
            HttpServletRequest req,
            @RequestBody ApplicationCreationRequest request
    ) {
        Long userId = extractUserIdFromRequest(req);
        return ResponseEntity.ok(service.createApplication(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<ApplicationResponse>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }
}

