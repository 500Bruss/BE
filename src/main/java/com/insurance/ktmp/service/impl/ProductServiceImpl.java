package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Category;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.enums.ProductStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.ProductMapper;
import com.insurance.ktmp.repository.AddonRepository;
import com.insurance.ktmp.repository.CategoryRepository;
import com.insurance.ktmp.repository.ProductRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.IProductService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final List<String> SEARCH_FIELDS = List.of("status");
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AddonRepository addonRepository;
    private final ProductMapper productMapper;

    @Override
    public RestResponse<ListResponse<ProductResponse>> getListProductsByFilter(int page, int size, String sort, String filter, String search, boolean all) {
        Specification<Product> sortable = RSQLJPASupport.toSort(sort);
        Specification<Product> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<Product> searchable = SearchHelper.parseSearchToken(search, SEARCH_FIELDS);
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<ProductResponse> responses = productRepository
                .findAll(sortable.and(filterable).and(searchable), pageable)
                .map(productMapper::toProductResponse);

        return RestResponse.ok(ListResponse.of(responses));
    }

    @Override
    public RestResponse<ProductResponse> createProduct(Long userId, ProductCreationRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASOURCE_NOT_FOUND));

        Product product = Product.builder()
                .id(IdGenerator.generateRandomId())
                .name(request.getName())
                .category(category)
                .status(ProductStatus.ACTIVE.name())
                .price(request.getPrice())
                .description(request.getDescription())
                .visible(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .updatedBy(user)
                .build();

//        List<Addon> addonList = new ArrayList<>();
//        for (AddonsCreationRequest addOnItem : request.getListAddOns()) {
//            Addon addon = Addon.builder()
//                    .id(IdGenerator.generateRandomId())
//                    .product(product)
//                    .code(addOnItem.getCode())
//                    .name(addOnItem.getName())
//                    .description(addOnItem.getDescription())
//                    .price(addOnItem.getPrice())
//                    .active(true)
//                    .metadata(addOnItem.getMetaData())
//                    .build();
//            addonList.add(addon);
//        }
//        addonRepository.saveAll(addonList);
        productRepository.save(product);

        return RestResponse.ok(productMapper.toProductResponse(product));
    }


//    private final ProductRepository productRepo;
//    private final CategoryRepository categoryRepo;
//
//    @Override
//    public Product create(Product p) {
//        log.info("Creating new product: {}", p.getName());
//
//        if (p.getCategory() == null || p.getCategory().getId() == null)
//            throw new ApiException("Category is required");
//
//        Category cat = categoryRepo.findById(p.getCategory().getId())
//                .orElseThrow(() -> new NotFoundException("Category not found"));
//
//        if (productRepo.existsByCode(p.getCode()))
//            throw new ApiException("Product code already exists");
//
//        p.setCategory(cat);
//        p.setActive(true);
//        return productRepo.save(p);
//    }
//
//    @Override
//    public Product update(Long id, Product p) {
//        Product ex = productRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Product not found"));
//
//        if (p.getName() != null) ex.setName(p.getName());
//        if (p.getDescription() != null) ex.setDescription(p.getDescription());
//        if (p.getBasePrice() != null) ex.setBasePrice(p.getBasePrice());
//        if (p.getActive() != null) ex.setActive(p.getActive());
//
//        // Update category nếu có truyền vào
//        if (p.getCategory() != null && p.getCategory().getId() != null) {
//            Category cat = categoryRepo.findById(p.getCategory().getId())
//                    .orElseThrow(() -> new NotFoundException("Category not found"));
//            ex.setCategory(cat);
//        }
//
//        log.info("Updated product: {}", ex.getId());
//        return productRepo.save(ex);
//    }
//
//    @Override
//    public void delete(Long id) {
//        Product p = productRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Product not found"));
//        // (Optionally: check liên kết policies/quotations)
//
//        log.warn("Deleting product id={}", id);
//        productRepo.delete(p);
//    }
//
//    @Override
//    public Product getById(Long id) {
//        return productRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Product not found"));
//    }
//
//    @Override
//    public List<Product> listAll() {
//        return productRepo.findByActiveTrue();
//    }
}
