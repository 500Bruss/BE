package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ClaimCreationRequest;
import com.insurance.ktmp.dto.request.ClaimReviewRequest;
import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.PolicyResponse;
import com.insurance.ktmp.repository.ClaimRepository;
import com.insurance.ktmp.service.IClaimService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController extends BaseController {
    private final IClaimService claimService;

    @PostMapping("/{policyId}")
    public ResponseEntity<RestResponse<ClaimResponse>> createClaim(
            @PathVariable Long policyId,
            @RequestBody ClaimCreationRequest request,
            HttpServletRequest httpRequest
            ) {
        Long userId = extractUserIdFromRequest(httpRequest);
        RestResponse<ClaimResponse> response = claimService.createClaim(userId, policyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<RestResponse<ListResponse<ClaimResponse>>> getClaims(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) boolean all
    ) {
        return ResponseEntity.ok(
                claimService.getAllClaimsByFilter(page, size, sort, filter, search, all)
        );
    }

    @PutMapping("/{claimId}")
    public ResponseEntity<RestResponse<ClaimResponse>> reviewClaim(
            @PathVariable Long claimId,
            @RequestBody ClaimReviewRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = extractUserIdFromRequest(httpRequest);
        RestResponse<ClaimResponse> response = claimService.reviewClaim(userId, claimId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
