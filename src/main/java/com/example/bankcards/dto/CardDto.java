package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private String id;
    private String maskedNumber;
    private String owner;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;
}