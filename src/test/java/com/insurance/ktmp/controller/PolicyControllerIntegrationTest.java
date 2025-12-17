package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.PolicyResponse;
import com.insurance.ktmp.service.IPolicyService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class PolicyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean
    private IPolicyService policyService;
    @MockBean private JwtTokenUtil jwtTokenUtil;

    private String token = "MOCK_ADMIN_TOKEN";
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

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldUpdateStatusSuccessfully() throws Exception {
        // Arrange
        Long policyId = 500L;
        String newStatus = "ACTIVE";

        when(policyService.updatePolicyStatus(eq(policyId), eq(newStatus), eq(MOCK_USER_ID), eq(false)))
                .thenReturn(RestResponse.ok("Policy updated to ACTIVE"));

        // Act & Assert
        mockMvc.perform(put("/api/policies/{id}/status/{status}", policyId, newStatus)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Policy updated to ACTIVE"));
    }

    @Test
    void shouldGetPolicyListWithFilters() throws Exception {
        // Arrange
        PolicyResponse p1 = PolicyResponse.builder().id("500").policyNumber("PL-1").build();
        ListResponse<PolicyResponse> listResponse = ListResponse.of(List.of(p1));

        when(policyService.getPolicyListByFilter(anyInt(), anyInt(), anyString(), any(), any(), anyBoolean()))
                .thenReturn(RestResponse.ok(listResponse));

        // Act & Assert
        mockMvc.perform(get("/api/policies")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "1")
                        .param("size", "5")
                        .param("search", "status==ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].id").value("500"));
    }
}