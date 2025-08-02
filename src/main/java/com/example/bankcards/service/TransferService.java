package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        Card sourceCard = cardRepository.findById(request.getSourceCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getSourceCardId()));
        Card targetCard = cardRepository.findById(request.getTargetCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getTargetCardId()));

        validateTransfer(sourceCard, targetCard, request.getAmount());

        sourceCard.setBalance(sourceCard.getBalance() - request.getAmount());
        targetCard.setBalance(targetCard.getBalance() + request.getAmount());

        Transaction transaction = Transaction.builder()
                .sourceCard(sourceCard)
                .targetCard(targetCard)
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .description(request.getDescription())
                .build();

        transactionRepository.save(transaction);

        return TransferResponse.builder()
                .transactionId(transaction.getId())
                .status("COMPLETED")
                .build();
    }

    private void validateTransfer(Card source, Card target, double amount) {
        if (source.getStatus() != CardStatus.ACTIVE || target.getStatus() != CardStatus.ACTIVE()) {
            throw new CardBlockedException("Card is not active");
        }
        if (source.getBalance() < amount) {
            throw new InsufficientBalanceException();
        }
        if (!source.getUser().getId().equals(target.getUser().getId())) {
            throw new UnauthorizedTransferException();
        }
    }
}