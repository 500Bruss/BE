package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.AddonsCreationRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.service.IAddonService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class AddonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean
    private IAddonService addonService;
    @MockBean private JwtTokenUtil jwtTokenUtil;

    private String token = "MOCK_ADMIN_TOKEN";
    private final Long MOCK_ADMIN_ID = 1L;
    private static final Long MOCK_USER_ID = 898454043L;

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
    void shouldCreateAddon_WhenAdminAuthenticated() throws Exception {
        // Arrange
        Long productId = 100L;
        AddonsCreationRequest request = AddonsCreationRequest.builder()
                .code("NEW_ADDON").name("Thủy kích").price(BigDecimal.valueOf(500)).build();

        when(addonService.createAddon(eq(MOCK_ADMIN_ID), eq(productId), any()))
                .thenReturn(RestResponse.ok(new ProductResponse()));

        // Act & Assert
        mockMvc.perform(post("/api/addons/{productId}", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAddonsByProductId() throws Exception {
        // Arrange
        Long productId = 100L;
        ListResponse<AddonsResponse> response = ListResponse.of(Collections.emptyList());
        when(addonService.getByProduct(productId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/addons")
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldUpdateStatus_UsingGetMethod() throws Exception {
        // Lưu ý: Controller của bạn đang dùng @GetMapping cho việc UPDATE status
        Long addonId = 500L;
        String status = "ACTIVE";

        when(addonService.updateAddonVisible(eq(addonId), eq(MOCK_ADMIN_ID), eq(status)))
                .thenReturn(RestResponse.ok("Success"));

        // Act & Assert
        mockMvc.perform(get("/api/addons/{addonId}/status/{status}", addonId, status)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
