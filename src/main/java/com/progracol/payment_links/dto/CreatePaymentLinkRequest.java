package com.progracol.payment_links.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;

@Data
public class CreatePaymentLinkRequest {
    @Positive(message = "Amount must be positive")
    private long amountCents;

    @NotBlank(message = "Currency is required")
    private String currency;  // Validar ISO en service

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Expires in minutes must be positive")
    private int expiresInMinutes;

    private Map<String, Object> metadata;
}