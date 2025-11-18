package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.PredefinedRole;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.request.CategoryCreationRequest;
import com.insurance.ktmp.dto.request.CategoryUpdateRequest;
import com.insurance.ktmp.dto.response.CategoryResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Category;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.CategoryStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.CategoryMapper;
import com.insurance.ktmp.repository.CategoryRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.ICategoryService;

import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements ICategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    UserRepository userRepository;

    private static final List<String> SEARCH_FIELDS = List.of("status");

    @Override
    public RestResponse<CategoryResponse> createCategory(CategoryCreationRequest request) {

        if (categoryRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.DATASOURCE_ALREADY_EXISTS);
        }

        Category category = new Category();
        category.setId(IdGenerator.generateRandomId());
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus().name());
        category.setMetadata(request.getMetaData());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);

        return RestResponse.ok(mapToResponse(category));
    }



    @Override
    public RestResponse<CategoryResponse> updateCategory(Long id, CategoryUpdateRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus().name());
        category.setMetadata(request.getMetaData());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);

        return RestResponse.ok(mapToResponse(category));
    }


    @Override
    public RestResponse<CategoryResponse> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return RestResponse.ok(mapToResponse(category));
    }
    @Override
    public RestResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> list = categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return RestResponse.ok(list);
    }

    @Override
    public RestResponse<Void> markAsInactive(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE));

        category.setStatus(CategoryStatus.INACTIVE.name());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);
        return RestResponse.ok(null);
    }
    @Override
    public RestResponse<Void> markAsActive(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE));

        category.setStatus(CategoryStatus.ACTIVE.name());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);
        return RestResponse.ok(null);
    }

    @Override
    public RestResponse<String> updateCategoryVisible(Long id, Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        category.setStatus(CategoryStatus.valueOf(status).name());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
        return RestResponse.ok("Category marked as {}". formatted(CategoryStatus.valueOf(status).name()));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .description(category.getDescription())
                .productStatus(null) // nếu có enum thì convert
                .metaData(category.getMetadata())
                .createdDate(category.getCreatedAt())
                .updatedDate(category.getUpdatedAt())
                .createBy(category.getCreatedBy() != null ? category.getCreatedBy().getId() : null)
                .updateBy(category.getUpdatedBy() != null ? category.getUpdatedBy().getId() : null)
                .build();
    }

    @Override
    public RestResponse<ListResponse<CategoryResponse>> getCategoryListByFilter(int page, int size, String sort, String filter, String search, boolean all) {
        Specification<Category> sortable = RSQLJPASupport.toSort(sort);
        Specification<Category> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<Category> searchable = SearchHelper.parseSearchToken(search, SEARCH_FIELDS);
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<CategoryResponse> responses = categoryRepository
                .findAll(sortable.and(filterable).and(searchable), pageable)
                .map(categoryMapper::toCategoryResponse);

        return RestResponse.ok(ListResponse.of(responses));
    }

}
