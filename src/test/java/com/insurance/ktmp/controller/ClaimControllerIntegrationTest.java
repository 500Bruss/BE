package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ClaimCreationRequest;
import com.insurance.ktmp.dto.request.ClaimReviewRequest;
import com.insurance.ktmp.dto.response.ClaimResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.service.IClaimService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class ClaimControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean
    private IClaimService claimService;
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
    void shouldCreateClaimSuccessfully() throws Exception {
        // Arrange
        Long policyId = 100L;
        ClaimCreationRequest request = ClaimCreationRequest.builder()
                .incidentDate("2023-10-10")
                .amountClaimed(BigDecimal.valueOf(1000))
                .claimData(Map.of("description", "Broken screen"))
                .build();

        when(claimService.createClaim(eq(MOCK_USER_ID), eq(policyId), any()))
                .thenReturn(RestResponse.ok(new ClaimResponse()));

        // Act & Assert
        mockMvc.perform(post("/api/claims/{policyId}", policyId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReviewClaimByAdmin() throws Exception {
        // Arrange
        Long claimId = 500L;
        ClaimReviewRequest reviewRequest = new ClaimReviewRequest("APPROVED");

        when(claimService.reviewClaim(eq(MOCK_USER_ID), eq(claimId), any()))
                .thenReturn(RestResponse.ok(new ClaimResponse()));

        // Act & Assert
        mockMvc.perform(put("/api/claims/{claimId}", claimId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated()); // Lưu ý: Code Controller của bạn trả về HttpStatus.CREATED cho PUT
    }

    @Test
    void shouldGetClaimsWithPagination() throws Exception {
        // Arrange
        when(claimService.getAllClaimsByFilter(anyInt(), anyInt(), anyString(), any(), any(), anyBoolean()))
                .thenReturn(RestResponse.ok(ListResponse.of(Collections.emptyList())));

        // Act & Assert
        mockMvc.perform(get("/api/claims")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}