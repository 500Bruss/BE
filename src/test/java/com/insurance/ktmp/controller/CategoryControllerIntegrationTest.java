package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.CategoryCreationRequest;
import com.insurance.ktmp.dto.response.CategoryResponse;
import com.insurance.ktmp.enums.CategoryStatus;
import com.insurance.ktmp.service.ICategoryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/test.properties")
public class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean
    private ICategoryService categoryService;
    @MockBean private JwtTokenUtil jwtTokenUtil;

    private String token = "MOCK_TOKEN";
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
    void shouldCreateCategorySuccessfully() throws Exception {
        // Arrange
        CategoryCreationRequest request = CategoryCreationRequest.builder()
                .code("TRAVEL").name("Travel Insurance").status(CategoryStatus.ACTIVE).build();

        when(categoryService.createCategory(any())).thenReturn(RestResponse.ok(new CategoryResponse()));

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCategoryById() throws Exception {
        // Arrange
        Long catId = 100L;
        when(categoryService.getCategoryById(catId)).thenReturn(RestResponse.ok(new CategoryResponse()));

        // Act & Assert
        mockMvc.perform(get("/api/categories/{id}", catId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateStatusViaPath() throws Exception {
        // Test endpoint: PUT /api/categories/{id}/status/{status}
        Long catId = 100L;
        String status = "ACTIVE";

        when(categoryService.updateCategoryStatus(eq(catId), eq(status), eq(MOCK_USER_ID), eq(false)))
                .thenReturn(RestResponse.ok("Updated"));

        mockMvc.perform(put("/api/categories/{id}/status/{status}", catId, status)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
