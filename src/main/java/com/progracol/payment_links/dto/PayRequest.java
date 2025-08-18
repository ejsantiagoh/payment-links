package com.progracol.payment_links.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PayRequest {
    @NotBlank(message = "Payment token is required")
    private String paymentToken;
}
