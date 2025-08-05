package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {
    @Autowired
    private CardRepository cardRepository;

    @Transactional
    public void transfer(String username, TransferRequestDto dto) {
        Card from = cardRepository.findById(dto.getFromCardId())
                .orElseThrow(() -> new IllegalArgumentException("Source card not found"));
        Card to = cardRepository.findById(dto.getToCardId())
                .orElseThrow(() -> new IllegalArgumentException("Target card not found"));

        if (!from.getOwner().getUsername().equals(username) || !to.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("One of the cards does not belong to you");
        }

        if (!from.getStatus().equals(CardStatus.ACTIVE) || !to.getStatus().equals(CardStatus.ACTIVE)) {
            throw new IllegalStateException("One of the cards is not active");
        }

        if (from.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(dto.getAmount()));
        to.setBalance(to.getBalance().add(dto.getAmount()));

        cardRepository.save(from);
        cardRepository.save(to);
    }
}
