package com.insurance.ktmp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.QuoteCreationRequest;
import com.insurance.ktmp.dto.response.QuoteResponse;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.entity.Quote;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.QuoteStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.QuoteMapper;
import com.insurance.ktmp.repository.ProductRepository;
import com.insurance.ktmp.repository.QuoteRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.IQuoteService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements IQuoteService {
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final QuoteMapper quoteMapper;

    @Override
    public RestResponse<QuoteResponse> createQuote(Long userId, QuoteCreationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        JsonNode inputNode;
        try {
            inputNode = objectMapper.readTree(request.getInputData());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INVALID_INPUT_JSON);
        }

        BigDecimal premium = calculatePremium(product.getCategory().getCode(), inputNode);

        Quote quote = Quote.builder()
                .id(IdGenerator.generateRandomId())
                .user(user)
                .product(product)
                .inputData(inputNode.toString())
                .premium(premium)
                .status(QuoteStatus.CALCULATED)
                .validUntil(LocalDateTime.now().plusMinutes(5))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();

        return  RestResponse.ok(quoteMapper.toQuoteResponse(quote));
    }

    private BigDecimal calculatePremium(String productType, JsonNode input) {
        try {
            switch (productType) {

                case "BHYT": // Bảo hiểm y tế
                    int age = input.get("age").asInt();
                    return BigDecimal.valueOf(age * 10);

                case "BHNT": // Bảo hiểm nhân thọ
                    int age2 = input.get("age").asInt();
                    boolean smoker = input.get("smoker").asBoolean(false);
                    BigDecimal base = BigDecimal.valueOf(age2 * 20);
                    return smoker ? base.multiply(BigDecimal.valueOf(1.3)) : base;

                case "BHPNT": // Bảo hiểm phi nhân thọ (xe)
                    BigDecimal vehicleValue = input.get("vehicle_value").decimalValue();
                    return vehicleValue.multiply(BigDecimal.valueOf(0.02));

                case "BHXH": // Bảo hiểm phi nhân thọ (xe)
                    BigDecimal salary = input.get("monthly_salary").decimalValue();
                    String status = input.get("employment_status").asText("full-time");
                    int workMonths = input.get("employment_duration").asInt(12);

                    // 10.5% lương tháng
                    BigDecimal rate = BigDecimal.valueOf(0.105);
                    BigDecimal premiumBHXH = salary.multiply(rate);

                    // part-time giảm 50%
                    if ("part-time".equalsIgnoreCase(status)) {
                        premiumBHXH = premiumBHXH.multiply(BigDecimal.valueOf(0.5));
                    }

                    // mới làm việc < 1 năm → ưu đãi giảm 20%
                    if (workMonths < 12) {
                        premiumBHXH = premiumBHXH.multiply(BigDecimal.valueOf(0.8));
                    }

                    return premiumBHXH.setScale(2, RoundingMode.HALF_UP);

                default:
                    return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_INPUT_JSON);
        }
    }

}
