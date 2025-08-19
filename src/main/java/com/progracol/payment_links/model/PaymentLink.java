package com.progracol.payment_links.model;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "payment_links", indexes = {
    @Index(name = "idx_merchant_status", columnList = "merchant_id, status"),
    @Index(name = "idx_links_created_at", columnList = "created_at"),
    @Index(name = "idx_links_amount", columnList = "amount_cents")
})
public class PaymentLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column(unique = true)
    private String reference;

    private long amountCents;

    private String currency;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    private LocalDateTime expiresAt;

    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name="created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> metadata;

    public enum Status {
        CREATED, PAID, CANCELLED, EXPIRED
    }
}
