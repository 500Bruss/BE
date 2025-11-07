package com.insurance.ktmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Quotation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;
    private Long customerId;
    private Double calculatedPremium;
    private String inputDataJson;
    private String status; // DRAFT, SUBMITTED
    private Instant createdAt;
}
