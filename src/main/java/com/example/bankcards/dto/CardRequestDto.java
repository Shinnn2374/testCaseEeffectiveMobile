package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardRequestDto {
    @NotBlank
    private String cardNumber;

    @NotNull
    private LocalDate expiryDate;
}
