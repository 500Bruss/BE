package com.insurance.ktmp.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime incidentDate;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @Column(columnDefinition = "JSON")
    private String claimData;

    @Column(precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) DEFAULT 0")
    private BigDecimal amountClaimed;

    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'SUBMITTED'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    @Column(columnDefinition = "TEXT")
    private String resolutionNote;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
