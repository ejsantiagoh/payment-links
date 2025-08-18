package com.progracol.payment_links.repository;

import com.progracol.payment_links.model.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, String> {
    Optional<PaymentAttempt> findByIdempotencyKey(String idempotencyKey);
}
