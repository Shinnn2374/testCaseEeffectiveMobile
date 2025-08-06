package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardRequest {
    @NotBlank
    private String ownerName;

    @Future
    private LocalDate expirationDate;

    @PositiveOrZero
    private BigDecimal balance;
}