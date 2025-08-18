package com.progracol.payment_links.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpirationJob {
    private final PaymentLinkService service;

    public ExpirationJob(PaymentLinkService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 */5 * * * *")  // Cada 5 minutos
    public void expire() {
        int expired = service.expireLinks();
        System.out.println("Expirados: " + expired);
    }
}