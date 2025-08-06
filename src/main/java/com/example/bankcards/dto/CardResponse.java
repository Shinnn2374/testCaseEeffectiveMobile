package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    private Long id;
    private String maskedNumber;  // "**** **** **** 1234"
    private String ownerName;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;
}