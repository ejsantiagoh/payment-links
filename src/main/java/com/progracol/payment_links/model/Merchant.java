package com.progracol.payment_links.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String apiKey;  // Clave API única

    private LocalDateTime createdAt = LocalDateTime.now();
}