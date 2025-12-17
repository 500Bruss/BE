package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.PaymentCreationRequest;
import com.insurance.ktmp.dto.response.PaymentResponse;
import com.insurance.ktmp.enums.PaymentMethod;
import com.insurance.ktmp.enums.PaymentStatus;
import com.insurance.ktmp.service.IPaymentService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean
    private IPaymentService paymentService;
    @MockBean private JwtTokenUtil jwtTokenUtil;

    private String token = "MOCK_TOKEN";
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    public void setup() throws Exception {
        // 1. Mock JwtTokenUtil để BaseController.extractUserIdFromRequest() không bị lỗi.
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(MOCK_USER_ID);

        // 2. Thực hiện đăng nhập (hoặc giả lập) để có token
        // (Sử dụng logic tương tự như bạn đã làm ở ProductControllerIntegrationTest)
        String username = "admin";
        String password = "admin";
        String loginRequestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreatePaymentSuccessfully() throws Exception {
        // Arrange
        PaymentCreationRequest request = new PaymentCreationRequest(100L, PaymentMethod.VNPAY);
        PaymentResponse response = PaymentResponse.builder()
                .paymentUrl("http://vnpay.vn/...")
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentService.createPayment(anyString(), eq(MOCK_USER_ID), any()))
                .thenReturn(RestResponse.ok(response));

        // Act & Assert
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.paymentUrl").value("http://vnpay.vn/..."));
    }

    @Test
    void shouldReturn401WhenUnauthorized() throws Exception {
        // Act & Assert (Không truyền header Authorization)
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}