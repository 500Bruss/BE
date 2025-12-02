package com.insurance.ktmp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.QuoteCreationRequest;
import com.insurance.ktmp.dto.response.QuoteResponse;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.entity.Addon;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.entity.Quote;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.QuoteStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.QuoteMapper;
import com.insurance.ktmp.repository.AddonRepository;
import com.insurance.ktmp.repository.ProductRepository;
import com.insurance.ktmp.repository.QuoteRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.IQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements IQuoteService {

    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddonRepository addonRepository;
    private final ObjectMapper objectMapper;
    private final QuoteMapper quoteMapper;

    @Override
    public RestResponse<QuoteResponse> createQuote(Long userId, QuoteCreationRequest request) {

        /* ========== CHECK USER ========== */
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        /* ========== EXPIRE QUOTE CŨ ========== */
        Optional<Quote> existed = quoteRepository.findByUser_IdAndStatus(userId, QuoteStatus.CALCULATED);
        if (existed.isPresent()) {
            existed.get().setStatus(QuoteStatus.EXPIRED);
            existed.get().setUpdatedAt(LocalDateTime.now());
            quoteRepository.save(existed.get());
        }

        /* ========== LOAD PRODUCT ========== */
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        /* ========== PARSE INPUTDATA ========== */
        JsonNode inputNode;
        try {
            inputNode = objectMapper.readTree(request.getInputData());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INVALID_INPUT_JSON);
        }

        /* ========== TÍNH PHÍ SẢN PHẨM (PREMIUM) ========== */
        BigDecimal basePremium = product.getPrice()
                .add(calculatePremium(product.getCategory().getCode(), inputNode));

        /* ========== TÍNH PHÍ ADDON ========== */
        BigDecimal addonTotal = BigDecimal.ZERO;

        List<Long> addonIds = request.getSelectedAddons();

        if (addonIds != null && !addonIds.isEmpty()) {
            List<Addon> addons = addonRepository.findAllById(addonIds);

            addonTotal = addons.stream()
                    .map(Addon::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        /* ========== PREMIUM CUỐI CÙNG ========== */
        BigDecimal finalPremium = basePremium.add(addonTotal);

        /* ========== LƯU QUOTE ========== */
        /* ========== LƯU QUOTE ========== */
        Quote quote = Quote.builder()
                .id(IdGenerator.generateRandomId())
                .user(user)
                .product(product)
                .inputData(inputNode.toString())
                .addonIds(writeJsonSafe(addonIds))   // lưu addon đã chọn
                .premium(finalPremium)
                .status(QuoteStatus.CALCULATED)
                .validUntil(LocalDateTime.now().plusMinutes(5))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();

        Quote saved = quoteRepository.save(quote);

        /* ========== MAP QUOTE → RESPONSE ========== */
        QuoteResponse response = quoteMapper.toQuoteResponse(saved);

        /* ========== GẮN DANH SÁCH ADDON ĐÃ CHỌN ========== */
        if (addonIds != null && !addonIds.isEmpty()) {
            List<Addon> addons = addonRepository.findAllById(addonIds);

            response.setSelectedAddons(
                    addons.stream().map(a -> AddonsResponse.builder()
                            .id(a.getId().toString())
                            .name(a.getName())
                            .description(a.getDescription())
                            .price(a.getPrice())
                            .metaData(a.getMetaData())
                            .build()
                    ).toList()
            );
        } else {
            response.setSelectedAddons(List.of());
        }

        return RestResponse.ok(response);

    }


    /* ========== SAFE JSON WRITE LIST ========== */
    private String writeJsonSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    /* ========== TÍNH PHÍ SẢN PHẨM GỐC ========== */
    private BigDecimal calculatePremium(String productType, JsonNode input) {
        try {
            switch (productType) {

                case "HLT":
                case "CAR":
                case "MOTO":
                case "BHYT":
                    int age = input.get("age").asInt();
                    return BigDecimal.valueOf(age * 10);

                case "BHNT":
                    int age2 = input.get("age").asInt();
                    boolean smoker = input.path("smoker").asBoolean(false);
                    BigDecimal base = BigDecimal.valueOf(age2 * 20);
                    return smoker ? base.multiply(BigDecimal.valueOf(1.3)) : base;

                case "BHPNT":
                    BigDecimal vehicleValue = input.get("vehicle_value").decimalValue();
                    return vehicleValue.multiply(BigDecimal.valueOf(0.02));

                case "BHXH":
                    BigDecimal salary = input.get("monthly_salary").decimalValue();
                    String status = input.get("employment_status").asText("full-time");
                    int workMonths = input.get("employment_duration").asInt(12);

                    BigDecimal rate = BigDecimal.valueOf(0.105);
                    BigDecimal premiumBHXH = salary.multiply(rate);

                    if ("part-time".equalsIgnoreCase(status)) {
                        premiumBHXH = premiumBHXH.multiply(BigDecimal.valueOf(0.5));
                    }

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
