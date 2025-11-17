package com.insurance.ktmp.service.impl;

import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.request.AddonsCreationRequest;
import com.insurance.ktmp.dto.request.ApplicationCreationRequest;
import com.insurance.ktmp.dto.response.ApplicationResponse;
import com.insurance.ktmp.dto.request.ProductCreationRequest;
import com.insurance.ktmp.dto.request.ProductUpdateRequest;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.dto.response.ProductResponse;
import com.insurance.ktmp.entity.Addon;
import com.insurance.ktmp.entity.Category;
import com.insurance.ktmp.entity.Quote;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.entity.Application;
import com.insurance.ktmp.enums.ProductStatus;
import com.insurance.ktmp.exception.ApiException;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.exception.NotFoundException;
import com.insurance.ktmp.mapper.ApplicationMapper;
import com.insurance.ktmp.repository.AddonRepository;
import com.insurance.ktmp.repository.CategoryRepository;
import com.insurance.ktmp.repository.ProductRepository;
import com.insurance.ktmp.repository.ApplicationRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.IApplicationService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements IApplicationService {

    private final ApplicationRepository appRepo;
    //private final QuoteRepository quoteRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final ApplicationMapper mapper;

    @Override
    public RestResponse<ApplicationResponse> createApplication(Long userId, ApplicationCreationRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode. USER_NOT_EXISTED));

        Quote quote = null;
        if (request.getQuoteId() != null) {
            quote = new Quote();
            quote.setId(request.getQuoteId());
        }
//        if (request.getQuoteId() != null) {
//            quote = quoteRepo.findById(request.getQuoteId())
//                    .orElseThrow(() -> new AppException(ErrorCode.QUOTE_NOT_FOUND));
//        }

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Application app = new Application();
        app.setId(IdGenerator.generateRandomId());
        app.setUser(user);
        app.setQuote(quote);
        app.setProduct(product);
        app.setApplicantData(request.getApplicantData());
        app.setInsuredData(request.getInsuredData());
        app.setTotalPremium(request.getTotalPremium());
        app.setStatus("PENDING");
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        appRepo.save(app);

        return RestResponse.ok(mapper.toResponse(app));
    }

    @Override
    public RestResponse<ApplicationResponse> getById(Long id) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        return RestResponse.ok(mapper.toResponse(app));
    }
}

