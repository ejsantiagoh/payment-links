package com.progracol.payment_links.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_attempts")
public class PaymentAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "payment_link_id")
    private PaymentLink paymentLink;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String reason;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(unique = true)
    private String idempotencyKey;

    public enum Status {
        SUCCESS, FAILED
    }
}