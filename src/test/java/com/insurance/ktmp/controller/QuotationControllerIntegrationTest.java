package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.QuoteCreationRequest;
import com.insurance.ktmp.dto.response.QuoteResponse;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.service.IQuoteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class QuotationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Thay thế bean IQuoteService thực bằng một Mock
    @MockBean
    private IQuoteService quoteService;

    // Thay thế bean JwtTokenUtil thực để mock extractUserId
    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private String token;
    private static final Long MOCK_USER_ID = 898454043L; // ID giả định trích xuất từ token

    @BeforeEach
    public void setup() throws Exception {
        // 1. Mock JwtTokenUtil để BaseController.extractUserIdFromRequest() không bị lỗi.
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(MOCK_USER_ID);

        // 2. Thực hiện đăng nhập (hoặc giả lập) để có token
        // (Sử dụng logic tương tự như bạn đã làm ở ProductControllerIntegrationTest)
        String username = "admin";
        String password = "admin";
        String loginRequestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(response).get("token").asText();
    }

    /**
     * Test case: Tạo Quote thành công (HTTP 201 Created)
     */
    @Test
    void shouldCreateQuoteSuccessfully() throws Exception {
        // Arrange
        QuoteCreationRequest quoteRequest = new QuoteCreationRequest(
                100L,
                "{\"age\": 30}",
                List.of(201L)
        );
        QuoteResponse quoteResponse = QuoteResponse.builder()
                .id("Q123")
                .premium(BigDecimal.valueOf(1000))
                .build();

        // Mock Service: Kiểm tra rằng Service được gọi với User ID đã trích xuất từ token
        when(quoteService.createQuote(eq(MOCK_USER_ID), any(QuoteCreationRequest.class)))
                .thenReturn(RestResponse.ok(quoteResponse));

        // Act & Assert (Dùng MockMvc)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/quotations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quoteRequest)))
                .andExpect(status().isCreated()) // HTTP Status 201
                .andExpect(jsonPath("$.data.id").value("Q123"))
                .andExpect(jsonPath("$.data.premium").value(1000));

        // Verify: Đảm bảo Service được gọi đúng
        verify(quoteService).createQuote(eq(MOCK_USER_ID), any(QuoteCreationRequest.class));
    }

    /**
     * Test case: Tạo Quote thất bại do lỗi nghiệp vụ (ví dụ: Sản phẩm không tồn tại)
     * (Giả định AppException được map về HTTP 404 Not Found)
     */
    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Arrange
        QuoteCreationRequest quoteRequest = new QuoteCreationRequest(
                999L, // Product ID không tồn tại
                "{\"age\": 30}",
                Collections.emptyList()
        );

        // Mock Service: Ném ra AppException(DATASOURCE_NOT_FOUND)
        when(quoteService.createQuote(eq(MOCK_USER_ID), any(QuoteCreationRequest.class)))
                .thenThrow(new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/quotations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quoteRequest)))
                .andExpect(status().isBadRequest()) // Giả định map DATASOURCE_NOT_FOUND -> 404
                .andExpect(jsonPath("$.message").value(ErrorCode.DATASOURCE_NOT_FOUND.getMessage()));

        // Verify: Đảm bảo Service được gọi
        verify(quoteService).createQuote(eq(MOCK_USER_ID), any(QuoteCreationRequest.class));
    }

    /**
     * Test case: Lỗi Validation trong Request Body (ví dụ: trường productId là null)
     * (Cần thêm @Valid vào Controller nếu chưa có, và thêm @NotNull vào QuoteCreationRequest)
     *
     * Giả định QuoteCreationRequest có validation (ví dụ: @NotNull Long productId)
     */
    @Test
    void shouldReturn400WhenInvalidRequestBody() throws Exception {
        // Arrange
        // Giả định productId là bắt buộc nhưng lại là null
        QuoteCreationRequest invalidRequest = new QuoteCreationRequest(
                null,
                "{}",
                Collections.emptyList()
        );

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/quotations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // HTTP Status 400

        // Verify: Service không được gọi nếu request validation fail
        verify(quoteService, never()).createQuote(any(), any());
    }
}