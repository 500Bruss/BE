package com.insurance.ktmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClaimRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    @ManyToOne
    private Policy policy;
    private String description;
    private String status; // SUBMITTED, VERIFIED, APPROVED, REJECTED
    private Instant createdAt;
}
