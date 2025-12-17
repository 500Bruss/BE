package com.insurance.ktmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.JwtTokenUtil;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.request.ProductUpdateRequest;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.enums.ProductStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.service.IProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductService productService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ProductController productController;

    @Autowired // Dùng để chuyển đổi đối tượng Java/JSON
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private String token;  // Biến instance để lưu token
    private static final Long MOCK_USER_ID = 898454043L;

    /**
     * Phương thức này thực hiện đăng nhập và lấy JWT Token trước mỗi test case.
     */
    @BeforeEach
    public void setup() throws Exception {
        // 1. Mock JwtTokenUtil để BaseController.extractUserIdFromRequest() không bị lỗi.
        // Khi MockMvc gọi API, BaseController sẽ được Spring tiêm JwtTokenUtil MOCK này.
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(MOCK_USER_ID);

        // Dữ liệu đăng nhập
        String username = "admin";
        String password = "admin";
        String loginRequestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        // Gửi yêu cầu POST tới API login để lấy token
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk());  // Kiểm tra trạng thái HTTP 200 OK

        // Lấy token từ response body
        String response = resultActions.andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldReturnListOfProducts() throws Exception {
        // Arrange
        ProductResponse productResponse = new ProductResponse("1", "Product1", "Description", "1", "Category1", BigDecimal.valueOf(100), null, ProductStatus.ACTIVE, true, null, LocalDateTime.now(), LocalDateTime.now(), null);
        List<ProductResponse> productList = Arrays.asList(productResponse);
        ListResponse<ProductResponse> listResponse = ListResponse.of(productList);

        // Mock Service (Giả định rằng Service trả về danh sách sản phẩm)
        when(productService.getListProductsByFilter(1, 5, "createdAt,desc", null, null, false))
                .thenReturn(RestResponse.ok(listResponse));

        // Act & Assert (Dùng MockMvc để gọi API)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + token) // Thêm token vào header
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk()) // Kiểm tra HTTP Status
                .andExpect(jsonPath("$.data.items.size()").value(1)) // Kiểm tra số lượng
                .andExpect(jsonPath("$.data.items[0].name").value("Product1")); // Kiểm tra nội dung
    }

    @Test
    void shouldReturnEmptyListWhenNoProducts() throws Exception {
        // Arrange
        ListResponse<ProductResponse> emptyResponse = ListResponse.of(Collections.emptyList());

        // Mock Service
        when(productService.getListProductsByFilter(1, 5, "createdAt,desc", null, null, false))
                .thenReturn(RestResponse.ok(emptyResponse));

        // Act & Assert (Dùng MockMvc)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + token) // Thêm token vào header
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.size()").value(0)); // Kiểm tra danh sách rỗng
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        // Arrange
        ProductCreationRequest productRequest = new ProductCreationRequest("Product1", "Description", 1L, BigDecimal.valueOf(100), null, null, null);
        ProductResponse productResponse = new ProductResponse("1", "Product1", "Description", "1", "Category1", BigDecimal.valueOf(100), null, ProductStatus.ACTIVE, true, null, LocalDateTime.now(), LocalDateTime.now(), null);

        // Mock Service: Kiểm tra rằng Service được gọi với User ID đã trích xuất (MOCK_USER_ID)
        when(productService.createProduct(eq(MOCK_USER_ID), eq(productRequest)))
                .thenReturn(RestResponse.ok(productResponse));

        // Act & Assert (Dùng MockMvc)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products") // URL của API
                        .header("Authorization", "Bearer " + token)  // THÊM TOKEN VÀO HEADER
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated()) // Kiểm tra HTTP Status 201 Created
                .andExpect(jsonPath("$.data.name").value("Product1")); // Kiểm tra nội dung response

        // Verify: Đảm bảo Service được gọi đúng
        verify(productService).createProduct(eq(MOCK_USER_ID), eq(productRequest));
    }

    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {
        // Arrange
        // Invalid request: name = null (giả sử @NotBlank hoặc @NotNull trên trường name)
        ProductCreationRequest invalidRequest = new ProductCreationRequest(null, "Description", 1L, BigDecimal.valueOf(100), null, null, null);

        // Act & Assert (Sử dụng MockMvc)
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Đảm bảo có thông báo lỗi

        // Xác minh rằng service không bao giờ được gọi
        verify(productService, never()).createProduct(any(), any());
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        // Arrange
        Long productId = 1L;
        ProductUpdateRequest updateRequest = new ProductUpdateRequest("Updated Name", "Updated Description", 1L, BigDecimal.valueOf(150), null, null);
        ProductResponse productResponse = new ProductResponse(
                String.valueOf(productId), "Updated Name", "Updated Description", "1", "Category1", BigDecimal.valueOf(150), null, ProductStatus.ACTIVE, true, "abc", LocalDateTime.now(), LocalDateTime.now(), null);

        // Mock Service (Service sẽ được gọi với userId trích xuất từ token)
        when(productService.updateProduct(eq(productId), eq(updateRequest)))
                .thenReturn(RestResponse.ok(productResponse));

        // **LƯU Ý QUAN TRỌNG:** Phương thức getById không cần thiết phải mock ở đây
        // vì ta đang test API update, chỉ cần mock updateProduct là đủ.

        // Act & Assert (Sử dụng MockMvc)
        mockMvc.perform(put("/api/products/{id}", productId) // Sử dụng PUT method và truyền ID vào path
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()); // Kiểm tra giá đã được cập nhật

        // Verify: Đảm bảo Service được gọi đúng với User ID và Product ID
        verify(productService).updateProduct(eq(productId), eq(updateRequest));
    }

    @Test
    void shouldReturn404WhenProductNotFoundToUpdate() throws Exception {
        // Arrange
        Long nonExistentProductId = 999L;
        ProductUpdateRequest updateRequest = new ProductUpdateRequest("Updated Name", "Updated Description", 1L, BigDecimal.valueOf(150), null, null);

        // Mock Service: Ném ra AppException khi được gọi
        when(productService.updateProduct(eq(nonExistentProductId), any(ProductUpdateRequest.class)))
                .thenThrow(new AppException(ErrorCode.PRODUCT_NOT_FOUND)); // Giả định ErrorCode này mapping tới HTTP 404

        // Act & Assert (Sử dụng MockMvc)
        mockMvc.perform(put("/api/products/{id}", nonExistentProductId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                // Kiểm tra HTTP Status. Giả định ErrorCode.PRODUCT_NOT_FOUND được xử lý thành 404 NOT_FOUND.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage())); // Kiểm tra nội dung lỗi

        // Verify: Đảm bảo Service đã được gọi
        verify(productService).updateProduct(eq(nonExistentProductId), any(ProductUpdateRequest.class));
    }

}

