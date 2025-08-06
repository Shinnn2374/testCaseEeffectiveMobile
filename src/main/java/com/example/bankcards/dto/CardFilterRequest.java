package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
public class CardFilterRequest {
    private CardStatus status;
    private String ownerName;
    private LocalDate expirationDateFrom;
    private LocalDate expirationDateTo;

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public LocalDate getExpirationDateFrom() {
        return expirationDateFrom;
    }

    public void setExpirationDateFrom(LocalDate expirationDateFrom) {
        this.expirationDateFrom = expirationDateFrom;
    }

    public LocalDate getExpirationDateTo() {
        return expirationDateTo;
    }

    public void setExpirationDateTo(LocalDate expirationDateTo) {
        this.expirationDateTo = expirationDateTo;
    }
}