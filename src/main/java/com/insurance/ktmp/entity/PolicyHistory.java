package com.insurance.ktmp.entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "policy_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyHistory {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(length = 50)
    private String oldStatus;

    @Column(length = 50)
    private String newStatus;

    @Column(length = 500)
    private String note;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
