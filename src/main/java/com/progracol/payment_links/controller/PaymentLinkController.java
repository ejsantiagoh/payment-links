package com.progracol.payment_links.controller;

import com.progracol.payment_links.dto.*;
import com.progracol.payment_links.service.PaymentLinkService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-links")
public class PaymentLinkController {

    private final PaymentLinkService service;

    public PaymentLinkController(PaymentLinkService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentLinkResponse> create(@Valid @RequestBody CreatePaymentLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPaymentLink(request));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentLinkResponse>> list(@RequestParam(required = false) String status,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.listPaymentLinks(status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentLinkResponse> get(@PathVariable String id) {
        return ResponseEntity.ok(service.getPaymentLink(id));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<PaymentLinkResponse> pay(@PathVariable String id,
                                                   @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                   @RequestBody PayRequest request) {
        return ResponseEntity.ok(service.payPaymentLink(id, idempotencyKey, request.getPaymentToken()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentLinkResponse> cancel(@PathVariable String id) {
        return ResponseEntity.ok(service.cancelPaymentLink(id));
    }
}
