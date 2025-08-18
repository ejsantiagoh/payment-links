package com.progracol.payment_links.service;

import com.progracol.payment_links.dto.CreatePaymentLinkRequest;
import com.progracol.payment_links.dto.PaymentLinkResponse;
import com.progracol.payment_links.exception.CustomException;
import com.progracol.payment_links.model.Merchant;
import com.progracol.payment_links.model.PaymentAttempt;
import com.progracol.payment_links.model.PaymentLink;
import com.progracol.payment_links.repository.MerchantRepository;
import com.progracol.payment_links.repository.PaymentAttemptRepository;
import com.progracol.payment_links.repository.PaymentLinkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentLinkService {

    private final PaymentLinkRepository paymentLinkRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final MerchantRepository merchantRepository;

    public PaymentLinkService(PaymentLinkRepository paymentLinkRepository, PaymentAttemptRepository paymentAttemptRepository, MerchantRepository merchantRepository) {
        this.paymentLinkRepository = paymentLinkRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.merchantRepository = merchantRepository;
    }

    private Merchant getCurrentMerchant() {
        return (Merchant) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public PaymentLinkResponse createPaymentLink(CreatePaymentLinkRequest request) {
        if (request.getAmountCents() <= 0) throw new CustomException(422, "Amount must be > 0", "VALIDATION_ERROR");
        if (!isValidCurrency(request.getCurrency())) throw new CustomException(422, "Currency must be valid ISO 4217", "VALIDATION_ERROR");
        // Otras validaciones: description length < 255, etc.

        Merchant merchant = getCurrentMerchant();
        PaymentLink link = new PaymentLink();
        link.setMerchant(merchant);
        link.setReference(generateReference());
        link.setAmountCents(request.getAmountCents());
        link.setCurrency(request.getCurrency());
        link.setDescription(request.getDescription());
        link.setExpiresAt(LocalDateTime.now().plusMinutes(request.getExpiresInMinutes()));
        link.setMetadata(request.getMetadata());

        paymentLinkRepository.save(link);
        return mapToResponse(link);
    }

    private boolean isValidCurrency(String currency) {
        // Simple check: longitud 3, uppercase. Mejora con lista de ISO si quieres.
        return currency != null && currency.length() == 3 && currency.matches("[A-Z]+");
    }

    private String generateReference() {
        return "PL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-" + String.format("%06d", (int) (Math.random() * 1000000));
    }

    public Page<PaymentLinkResponse> listPaymentLinks(String status, Pageable pageable) {
        Merchant merchant = getCurrentMerchant();
        if (status != null) {
            try {
                PaymentLink.Status enumStatus = PaymentLink.Status.valueOf(status.toUpperCase());
                return paymentLinkRepository.findByMerchantIdAndStatus(merchant.getId(), enumStatus, pageable).map(this::mapToResponse);
            } catch (IllegalArgumentException e) {
                throw new CustomException(422, "Invalid status", "VALIDATION_ERROR");
            }
        }
        return paymentLinkRepository.findByMerchantId(merchant.getId(), pageable).map(this::mapToResponse);
    }

    public PaymentLinkResponse getPaymentLink(String identifier) {
        Merchant merchant = getCurrentMerchant();
        Optional<PaymentLink> optionalLink = paymentLinkRepository.findById(identifier);
        if (optionalLink.isEmpty()) {
            optionalLink = paymentLinkRepository.findByReference(identifier);  // Busca por reference si no es id
        }
        PaymentLink link = optionalLink.orElseThrow(() -> new CustomException(404, "Not found", "NOT_FOUND"));
        if (!link.getMerchant().getId().equals(merchant.getId())) throw new CustomException(403, "Forbidden", "FORBIDDEN");
        return mapToResponse(link);
    }

    @Transactional
    public PaymentLinkResponse payPaymentLink(String id, String idempotencyKey, String paymentToken) {
        Merchant merchant = getCurrentMerchant();
        PaymentLink link = paymentLinkRepository.findById(id).orElseThrow(() -> new CustomException(404, "Not found", "NOT_FOUND"));
        if (!link.getMerchant().getId().equals(merchant.getId())) throw new CustomException(403, "Forbidden", "FORBIDDEN");

        if (link.getStatus() != PaymentLink.Status.CREATED || link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(409, "Link no pagable", "CONFLICT");
        }

        Optional<PaymentAttempt> existing = paymentAttemptRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            PaymentAttempt attempt = existing.get();
            if (attempt.getStatus() == PaymentAttempt.Status.SUCCESS) {
                return mapToResponse(link);
            } else {
                throw new CustomException(409, attempt.getReason(), "CONFLICT");
            }
        }

        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setPaymentLink(link);
        attempt.setIdempotencyKey(idempotencyKey);
        if (paymentToken.startsWith("ok_")) {
            attempt.setStatus(PaymentAttempt.Status.SUCCESS);
            link.setStatus(PaymentLink.Status.PAID);
            link.setPaidAt(LocalDateTime.now());
        } else if (paymentToken.startsWith("fail_")) {
            attempt.setStatus(PaymentAttempt.Status.FAILED);
            attempt.setReason("Pago fallido simulado");
            paymentAttemptRepository.save(attempt);
            throw new CustomException(409, "Pago fallido", "CONFLICT");
        } else {
            throw new CustomException(422, "Token inválido", "VALIDATION_ERROR");
        }

        paymentAttemptRepository.save(attempt);
        paymentLinkRepository.save(link);
        return mapToResponse(link);
    }

    public PaymentLinkResponse cancelPaymentLink(String id) {
        Merchant merchant = getCurrentMerchant();
        PaymentLink link = paymentLinkRepository.findById(id).orElseThrow(() -> new CustomException(404, "Not found", "NOT_FOUND"));
        if (!link.getMerchant().getId().equals(merchant.getId())) throw new CustomException(403, "Forbidden", "FORBIDDEN");
        if (link.getStatus() != PaymentLink.Status.CREATED || link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(409, "No cancelable", "CONFLICT");
        }
        link.setStatus(PaymentLink.Status.CANCELLED);
        paymentLinkRepository.save(link);
        return mapToResponse(link);
    }

    @Transactional
    public int expireLinks() {
        List<PaymentLink> links = paymentLinkRepository.findByStatusAndExpiresAtBefore(PaymentLink.Status.CREATED, LocalDateTime.now());
        int count = 0;
        for (PaymentLink link : links) {
            link.setStatus(PaymentLink.Status.EXPIRED);
            paymentLinkRepository.save(link);
            count++;
        }
        return count;
    }

    private PaymentLinkResponse mapToResponse(PaymentLink link) {
        PaymentLinkResponse response = new PaymentLinkResponse();
        response.setId(link.getId());
        response.setReference(link.getReference());
        response.setAmountCents(link.getAmountCents());
        response.setCurrency(link.getCurrency());
        response.setDescription(link.getDescription());
        response.setStatus(link.getStatus().name());
        response.setExpiresAt(link.getExpiresAt());
        response.setPaidAt(link.getPaidAt());
        response.setMetadata(link.getMetadata());
        return response;
    }
}