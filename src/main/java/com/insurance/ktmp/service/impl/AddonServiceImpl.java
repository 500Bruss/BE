package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.request.AddonsCreationRequest;
import com.insurance.ktmp.dto.request.AddonsUpdateRequest;
import com.insurance.ktmp.dto.response.AddonsResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.entity.Addon;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.enums.AddOnsStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.AddonMapper;
import com.insurance.ktmp.repository.AddonRepository;
import com.insurance.ktmp.repository.ProductRepository;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddonServiceImpl implements IAddonService {

    AddonRepository addonRepository;
    ProductRepository productRepository;
    AddonMapper addonMapper;

    private static final List<String> SEARCH_FIELDS = List.of("status", "code", "name");

    @Override
    public RestResponse<AddonsResponse> createAddon(AddonsCreationRequest request) {

        if (addonRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Addon code already exists");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        Addon addon = new Addon();
        addon.setId(IdGenerator.generateRandomId());
        addon.setProduct(product);
        addon.setCode(request.getCode());
        addon.setName(request.getName());
        addon.setDescription(request.getDescription());
        addon.setPrice(request.getPrice());
        addon.setStatus(request.getStatus());
        addon.setMetaData(request.getMetaData());
        addon.setCreatedAt(LocalDateTime.now());
        addon.setUpdatedAt(LocalDateTime.now());

        addonRepository.save(addon);

        return RestResponse.ok(addonMapper.toAddonsResponse(addon));
    }

    @Override
    public RestResponse<AddonsResponse> updateAddon(Long id, AddonsUpdateRequest request) {

        Addon addon = addonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        addon.setName(request.getName());
        addon.setDescription(request.getDescription());
        addon.setPrice(request.getPrice());
        addon.setStatus(request.getStatus());
        addon.setMetaData(request.getMetaData());
        addon.setUpdatedAt(LocalDateTime.now());

        addonRepository.save(addon);

        return RestResponse.ok(addonMapper.toAddonsResponse(addon));
    }

    @Override
    public RestResponse<AddonsResponse> getAddonById(Long id) {
        Addon addon = addonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));
        return RestResponse.ok(addonMapper.toAddonsResponse(addon));
    }

    @Override
    public RestResponse<List<AddonsResponse>> getAllAddons() {
        List<AddonsResponse> list = addonRepository.findAll()
                .stream()
                .map(addonMapper::toAddonsResponse)
                .toList();

        return RestResponse.ok(list);
    }

    @Override
    public RestResponse<List<AddonsResponse>> getAddonsByProduct(Long productId) {
        List<AddonsResponse> list = addonRepository.findByProduct_Id(productId)
                .stream()
                .map(addonMapper::toAddonsResponse)
                .toList();
        return RestResponse.ok(list);
    }

    @Override
    public RestResponse<Void> markAsInactive(Long id) {
        Addon addon = addonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE));

        addon.setStatus(AddOnsStatus.INACTIVE);
        addon.setUpdatedAt(LocalDateTime.now());

        addonRepository.save(addon);
        return RestResponse.ok(null);
    }

    @Override
    public RestResponse<Void> markAsActive(Long id) {
        Addon addon = addonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_INVALID_SEQUENCE));

        addon.setStatus(AddOnsStatus.ACTIVE);
        addon.setUpdatedAt(LocalDateTime.now());

        addonRepository.save(addon);
        return RestResponse.ok(null);
    }

    @Override
    public RestResponse<ListResponse<AddonsResponse>> getAddonListByFilter(
            int page, int size, String sort, String filter, String search, boolean all
    ) {
        Specification<Addon> sortable = RSQLJPASupport.toSort(sort);
        Specification<Addon> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<Addon> searchable = SearchHelper.parseSearchToken(search, SEARCH_FIELDS);

        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);

        Page<AddonsResponse> responses = addonRepository
                .findAll(sortable.and(filterable).and(searchable), pageable)
                .map(addonMapper::toAddonsResponse);

        return RestResponse.ok(ListResponse.of(responses));
    }
}
