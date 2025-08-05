package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String encryptedNumber;

    @ManyToOne
    private User owner;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private LocalDate expiryDate;

    private BigDecimal balance;
}
