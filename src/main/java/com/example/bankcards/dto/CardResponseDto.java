package com.example.bankcards.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardResponseDto {
    private Long id;
    private String maskedNumber;
    private String status;
    private LocalDate expiryDate;
    private BigDecimal balance;
    private String ownerUsername;
}
