package com.example.bankcards.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CardRequest {
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Invalid card number format")
    private String cardNumber;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    @PositiveOrZero(message = "Balance cannot be negative")
    private double initialBalance;
}