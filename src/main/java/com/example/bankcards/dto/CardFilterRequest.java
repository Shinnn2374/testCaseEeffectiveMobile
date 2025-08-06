package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardFilterRequest {
    private CardStatus status;
    private String ownerName;
    private LocalDate expirationDateFrom;
    private LocalDate expirationDateTo;
}