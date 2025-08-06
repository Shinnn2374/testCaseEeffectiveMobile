package com.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long sourceCardId;
    private Long targetCardId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}