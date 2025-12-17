package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ApplicationCreationRequest;
import com.insurance.ktmp.dto.request.ApplicationStatusUpdateRequest;
import com.insurance.ktmp.dto.response.ApplicationResponse;
import com.insurance.ktmp.enums.ApplicationStatus;
import com.insurance.ktmp.service.IApplicationService;
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

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class ApplicationControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IApplicationService applicationService;
    @MockBean private JwtTokenUtil jwtTokenUtil;

    private String token = "MOCK_JWT_TOKEN";
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
    void shouldCreateApplicationSuccessfully() throws Exception {
        // Arrange
        Long quoteId = 100L;
        ApplicationCreationRequest request = new ApplicationCreationRequest(
                Map.of("name", "Applicant"),
                Map.of("name", "Insured")
        );
        ApplicationResponse response = new ApplicationResponse();

        when(applicationService.createApplication(eq(MOCK_USER_ID), eq(quoteId), any()))
                .thenReturn(RestResponse.ok(response));

        // Act & Assert
        mockMvc.perform(post("/api/applications/{quoteId}", quoteId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldGetApplicationById() throws Exception {
        // Arrange
        Long appId = 500L;
        when(applicationService.getById(appId)).thenReturn(RestResponse.ok(new ApplicationResponse()));

        // Act & Assert
        mockMvc.perform(get("/api/applications/{id}", appId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateApplicationStatus() throws Exception {
        // Arrange
        Long appId = 500L;
        ApplicationStatusUpdateRequest statusUpdate = new ApplicationStatusUpdateRequest();
        statusUpdate.setStatus(ApplicationStatus.APPROVED);

        when(applicationService.updateStatus(eq(appId), eq(ApplicationStatus.APPROVED)))
                .thenReturn(RestResponse.ok(new ApplicationResponse()));

        // Act & Assert
        mockMvc.perform(patch("/api/applications/{id}/status", appId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/applications/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized()); // Do Spring Security cấu hình
    }
}