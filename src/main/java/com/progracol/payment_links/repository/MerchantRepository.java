package com.progracol.payment_links.repository;

import com.progracol.payment_links.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    Optional<Merchant> findByApiKey(String apiKey);
}
