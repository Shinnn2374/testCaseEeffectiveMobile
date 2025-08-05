package com.example.bankcards.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponseDto {
    private Long id;
    private String maskedNumber;
    private String status;
    private LocalDate expiryDate;
    private BigDecimal balance;
    private String ownerUsername;
}
