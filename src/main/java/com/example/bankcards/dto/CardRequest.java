package com.example.bankcards.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CardRequest {
    @NotBlank
    private String cardNumber;
    @NotBlank
    private String ownerName;
    @Future
    private LocalDate expirationDate;
    @PositiveOrZero
    private double initialBalance;
    @NotNull
    private Long userId;
}