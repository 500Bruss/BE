package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.PolicyCreationRequest;
import com.insurance.ktmp.dto.response.PolicyResponse;
import com.insurance.ktmp.dto.response.ListResponse;

import com.insurance.ktmp.dto.response.ProductResponse;


import com.insurance.ktmp.service.IPolicyService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController extends BaseController {

    private final IPolicyService policyService;

    @PostMapping
    public ResponseEntity<RestResponse<PolicyResponse>> createPolicy(
            @RequestBody PolicyCreationRequest request,
            HttpServletRequest httpReq
    ) {
        Long userId = extractUserIdFromRequest(httpReq);
        RestResponse<PolicyResponse>response= policyService.createPolicy( request.getApplicationId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<PolicyResponse>> getPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    // ========== UPDATE STATUS ==========

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<RestResponse<String>> updateStatus(
            @PathVariable Long id,
            @PathVariable String status,
            HttpServletRequest req
    ) {
        Long userId = extractUserIdFromRequest(req);
        return ResponseEntity.ok(policyService.updatePolicyStatus(id, status, userId));

    }
    @GetMapping
    public ResponseEntity<RestResponse<ListResponse<PolicyResponse>>> getPolicyList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) boolean all
    ) {
        return ResponseEntity.ok(
                policyService.getPolicyListByFilter(page, size, sort, filter, search, all)
        );
    }

}
