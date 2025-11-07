package com.insurance.ktmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_request")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SupportRquest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String subject;
    @Column(length=2000)
    private String message;
    private String response; // admin response
    private String status; // OPEN, RESPONDED, CLOSED
    private Instant createdAt;
}
