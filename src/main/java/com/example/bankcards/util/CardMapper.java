package com.example.bankcards.util;

import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public CardResponseDto toDto(Card card) {
        CardResponseDto dto = new CardResponseDto();
        dto.setId(card.getId());
        dto.setMaskedNumber(mask(card.getEncryptedNumber()));
        dto.setStatus(card.getStatus().name());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setBalance(card.getBalance());
        dto.setOwnerUsername(card.getOwner().getUsername());
        return dto;
    }

    private String mask(String encrypted) {
        return "**** **** **** " + encrypted.substring(encrypted.length() - 4);
    }
}
