package com.progracol.payment_links.repository;

import com.progracol.payment_links.model.PaymentLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentLinkRepository extends JpaRepository<PaymentLink, String> {
    Page<PaymentLink> findByMerchantId(String merchantId, Pageable pageable);
    
    Optional<PaymentLink> findByReference(String reference);

    // Para filtros: ejemplo por status
    Page<PaymentLink> findByMerchantIdAndStatus(String merchantId, PaymentLink.Status status, Pageable pageable);

    // Para expiración
    @Query("SELECT p FROM PaymentLink p WHERE p.status = 'CREATED' AND p.expiresAt < ?1")
    List<PaymentLink> findByStatusAndExpiresAtBefore(PaymentLink.Status created, LocalDateTime now);
}