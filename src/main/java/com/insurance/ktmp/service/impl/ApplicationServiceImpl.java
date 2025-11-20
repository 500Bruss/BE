package com.insurance.ktmp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.ktmp.common.IdGenerator;
import com.insurance.ktmp.common.RestResponse;
import com.insurance.ktmp.common.SearchHelper;
import com.insurance.ktmp.dto.request.ApplicationCreationRequest;
import com.insurance.ktmp.dto.request.ApplicationApprovalRequest;
import com.insurance.ktmp.dto.response.ApplicationResponse;
import com.insurance.ktmp.dto.response.ListResponse;
import com.insurance.ktmp.entity.Application;
import com.insurance.ktmp.entity.Product;
import com.insurance.ktmp.entity.Quote;
import com.insurance.ktmp.entity.User;
import com.insurance.ktmp.entity.Policy;
import com.insurance.ktmp.entity.PolicyHistory;
import com.insurance.ktmp.enums.ApplicationStatus;
import com.insurance.ktmp.enums.QuoteStatus;
import com.insurance.ktmp.exception.AppException;
import com.insurance.ktmp.exception.ErrorCode;
import com.insurance.ktmp.mapper.ApplicationMapper;
import com.insurance.ktmp.repository.ApplicationRepository;

import com.insurance.ktmp.repository.QuoteRepository;
import com.insurance.ktmp.repository.PolicyRepository;
import com.insurance.ktmp.repository.HisPolicyRepository;
import com.insurance.ktmp.repository.UserRepository;
import com.insurance.ktmp.service.IApplicationService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements IApplicationService {

    private final ApplicationRepository appRepo;
    private final QuoteRepository quoteRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final ApplicationMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON converter

    private static final String[] APPLICATION_SEARCH_FIELDS = {
            "applicantData",
            "insuredData",
            "status"
    };
    @Scheduled(cron = "0 */5 * * * *")
    public void autoCancelApplications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusMinutes(5);

        List<Application> expiredApps =
                appRepo.findByStatusAndCreatedAtBefore(ApplicationStatus.SUBMITTED, threshold);

        if (expiredApps.isEmpty()) return;

        expiredApps.forEach(app -> {
            app.setStatus(ApplicationStatus.CANCELLED);
            app.setUpdatedAt(now);
        });

        appRepo.saveAll(expiredApps);
    }
    @Override
    public RestResponse<ListResponse<ApplicationResponse>> getListApplicationByFilter(
            int page,
            int size,
            String sort,
            String filter,
            String search,
            boolean all
    ) {
        Specification<Application> sortable   = RSQLJPASupport.toSort(sort);
        Specification<Application> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<Application> searchable = SearchHelper.parseSearchToken(search, List.of(APPLICATION_SEARCH_FIELDS));

        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);

        Page<ApplicationResponse> responses = appRepo
                .findAll(sortable.and(filterable).and(searchable), pageable)
                .map(mapper::toResponse);

        return RestResponse.ok(ListResponse.of(responses));
    }

    @Override
    public RestResponse<ApplicationResponse> createApplication(
            Long userId,
            Long quoteId,
            ApplicationCreationRequest request
    ) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Quote quote = quoteRepo.findByIdAndUser_IdAndStatus(quoteId, userId, QuoteStatus.CALCULATED)
                .orElseThrow(() -> new AppException(ErrorCode.QUOTE_NOT_FOUND));

        if (!quote.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Product product = quote.getProduct();
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // Convert JSON object -> String
        String applicantJson;
        String insuredJson;

        try {
            applicantJson = objectMapper.writeValueAsString(request.getApplicantData());
            insuredJson   = objectMapper.writeValueAsString(request.getInsuredData());
        } catch (Exception e) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Application app = new Application();
        app.setId(IdGenerator.generateRandomId());
        app.setUser(user);
        app.setQuote(quote);
        app.setProduct(product);

        app.setApplicantData(applicantJson);
        app.setInsuredData(insuredJson);

        app.setTotalPremium(quote.getPremium());
        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        appRepo.save(app);

        quote.setStatus(QuoteStatus.CONFIRMED);
        quote.setUpdatedAt(LocalDateTime.now());
        quoteRepo.save(quote);

        return RestResponse.ok(mapper.toResponse(app));
    }

    @Override
    public RestResponse<ApplicationResponse> updateStatus(Long id, ApplicationStatus status) {

        Application app = appRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        app.setStatus(ApplicationStatus.valueOf(status.name()));
        app.setUpdatedAt(LocalDateTime.now());

        appRepo.save(app);

        return RestResponse.ok(
              mapper.toResponse(app)
        );
    }

    @Override
    public RestResponse<ApplicationResponse> getById(Long id) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        return RestResponse.ok(mapper.toResponse(app));
    }








}
