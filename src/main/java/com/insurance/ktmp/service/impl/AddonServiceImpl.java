package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.PredefinedRole;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.request.AddonsCreationRequest;
import com.insurance.ktmp.dto.request.AddonsUpdateRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Addon;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.AddOnsStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.AddonMapper;
import com.insurance.ktmp.mapper.ProductMapper;
import com.insurance.ktmp.repository.AddonRepository;
import com.insurance.ktmp.repository.ProductRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.IAddonService;

import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.insurance.ktmp.dto.response.ListResponse;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddonServiceImpl implements IAddonService {

    AddonRepository addonRepository;
    ProductRepository productRepository;
    AddonMapper addonMapper;
    ProductMapper productMapper;
    UserRepository userRepository;

    @Override
    public RestResponse<ProductResponse> createAddon(Long userId, Long productId, AddonsCreationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        if (addonRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.DATASOURCE_NOT_FOUND);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        Addon addon = new Addon();
        addon.setId(IdGenerator.generateRandomId());
        addon.setProduct(product);
        addon.setCode(request.getCode());
        addon.setName(request.getName());
        addon.setDescription(request.getDescription());
        addon.setPrice(request.getPrice());
        addon.setStatus(AddOnsStatus.ACTIVE);
        addon.setMetaData(request.getMetaData());
        addon.setCreatedAt(LocalDateTime.now());
        addon.setUpdatedAt(LocalDateTime.now());

        addonRepository.save(addon);

        return RestResponse.ok(productMapper.toProductResponse(product)); //todo
    }
    @Override
    public RestResponse<ProductResponse> updateAddon(Long userId, Long id, AddonsUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        Addon addon = addonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        addon.setName(request.getName());
        addon.setDescription(request.getDescription());
        addon.setPrice(request.getPrice());
        addon.setStatus(AddOnsStatus.valueOf(request.getStatus().name()));
        addon.setMetaData(request.getMetaData());
        addon.setUpdatedAt(LocalDateTime.now());
        addonRepository.save(addon);

        Product product = productRepository.findById(addon.getProduct().getId())
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));


        return RestResponse.ok(productMapper.toProductResponse(product));
    }


    @Override
    public RestResponse<String> updateAddonVisible(Long id, Long userId, String status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getRole().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.ADMIN_ROLE))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_TO_UPDATE_THIS_RESOURCE);
        }

        // 3. Tìm Addon
        Addon addon = addonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        // 4. Update status (Enum AddOnsStatus)
        AddOnsStatus newStatus = AddOnsStatus.valueOf(status.toUpperCase());
        addon.setStatus(newStatus);

        addon.setUpdatedAt(LocalDateTime.now());
        addonRepository.save(addon);

        return RestResponse.ok(
                "Addon marked as %s".formatted(newStatus.name())
        );
    }

    @Override
    public ListResponse<AddonsResponse> getByProduct(Long productId) {
        List<Addon> list = addonRepository.findByProductId(productId);
        List<AddonsResponse> mapped = addonMapper.toResponseList(list);

        return ListResponse.of(mapped);
    }









}
