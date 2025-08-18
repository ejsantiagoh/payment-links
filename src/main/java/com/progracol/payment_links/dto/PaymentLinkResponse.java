package com.progracol.payment_links.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class PaymentLinkResponse {
    private String id;
    private String reference;
    private long amountCents;
    private String currency;
    private String description;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime paidAt;
    private Map<String, Object> metadata;
}