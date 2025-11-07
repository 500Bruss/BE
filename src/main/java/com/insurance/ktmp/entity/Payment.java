package com.insurance.ktmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentReference;

    @ManyToOne
    private Quotation quotation;

    private String method; // e.g., CREDIT_CARD, BANK_TRANSFER
    private String status; // PENDING, SUCCESS, FAILED

    private Double amount;
    private Instant createdAt;
    private Instant updatedAt;
}