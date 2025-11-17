package com.insurance.ktmp.controller;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.QuoteCreationRequest;
import com.insurance.ktmp.dto.response.QuoteResponse;
import com.insurance.ktmp.service.IQuoteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController extends BaseController {
    private final IQuoteService quoteService;

    @PostMapping
    public ResponseEntity<RestResponse<QuoteResponse>> createQuote(
            @RequestBody QuoteCreationRequest request,
            HttpServletRequest httpReq) {
        Long userId = extractUserIdFromRequest(httpReq);
        RestResponse<QuoteResponse> response = quoteService.createQuote(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //TODO
    @PutMapping("/{quoteId}")
    public ResponseEntity<RestResponse<QuoteResponse>> updateQuote(
            @PathVariable Long quoteId,
            HttpServletRequest httpReq
    ) {
        Long userId = extractUserIdFromRequest(httpReq);
        RestResponse<QuoteResponse> response = null;
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
