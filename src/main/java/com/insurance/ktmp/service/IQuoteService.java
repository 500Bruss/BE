package com.insurance.ktmp.service;

import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.QuoteCreationRequest;
import com.insurance.ktmp.dto.response.QuoteResponse;

public interface IQuoteService {
    RestResponse<QuoteResponse> createQuote(Long userId, QuoteCreationRequest request);
}
