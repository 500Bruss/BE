package com.insurance.ktmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "policies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Policy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String policyNumber;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private Long customerId; // for demo, can be simple Long
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE, CANCELLED
    private Double premium;
}
