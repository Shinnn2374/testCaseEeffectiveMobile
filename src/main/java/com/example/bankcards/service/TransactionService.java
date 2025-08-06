package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse transfer(Long userId, TransactionRequest request) {
        Card sourceCard = cardRepository.findById(request.getSourceCardId())
                .orElseThrow(() -> new EntityNotFoundException("Source card not found"));
        Card targetCard = cardRepository.findById(request.getTargetCardId())
                .orElseThrow(() -> new EntityNotFoundException("Target card not found"));

        validateTransfer(userId, sourceCard, targetCard, request.getAmount());

        sourceCard.setBalance(sourceCard.getBalance().subtract(request.getAmount()));
        targetCard.setBalance(targetCard.getBalance().add(request.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setSourceCard(sourceCard);
        transaction.setTargetCard(targetCard);
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    private void validateTransfer(Long userId, Card sourceCard, Card targetCard, BigDecimal amount) {
        if (!sourceCard.getUser().getId().equals(userId)) {
            throw new TransferException("Source card does not belong to the user");
        }
        if (!targetCard.getUser().getId().equals(userId)) {
            throw new SecurityException("Target card does not belong to the user");
        }
        if (sourceCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Source card is not active");
        }
        if (targetCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Target card is not active");
        }
        if (sourceCard.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .sourceCardId(transaction.getSourceCard().getId())
                .targetCardId(transaction.getTargetCard().getId())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}