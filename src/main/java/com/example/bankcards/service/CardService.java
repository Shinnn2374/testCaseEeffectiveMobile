package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    // Создание карты
    @Transactional
    public CardResponse createCard(CardRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        Card card = new Card();
        card.setNumber(generateCardNumber()); // Реализация генерации номера (см. ниже)
        card.setOwnerName(request.getOwnerName());
        card.setExpirationDate(request.getExpirationDate());
        card.setBalance(request.getBalance());
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);

        cardRepository.save(card);
        return mapToCardResponse(card);
    }

    public CardResponse getCardById(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getId().equals(userId)) {
            throw new SecurityException("User is not the owner of the card");
        }

        return mapToCardResponse(card);
    }

    public List<CardResponse> getUserCards(Long userId) {
        return cardRepository.findByUserId(userId)
                .stream()
                .map(this::mapToCardResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CardResponse blockCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getId().equals(userId)) {
            throw new SecurityException("User is not the owner of the card");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Card is already blocked");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return mapToCardResponse(card);
    }

    // Обновление баланса (для админа, позже добавим @PreAuthorize)
    @Transactional
    public CardResponse updateBalance(Long cardId, BigDecimal newBalance) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + cardId));

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        card.setBalance(newBalance);
        cardRepository.save(card);
        return mapToCardResponse(card);
    }

    private CardResponse mapToCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .maskedNumber(maskCardNumber(card.getNumber()))
                .ownerName(card.getOwnerName())
                .expirationDate(card.getExpirationDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }

    // Генерация номера карты (заглушка)
    private String generateCardNumber() {
        return String.format("%016d", (long) (Math.random() * 1_0000_0000_0000_0000L));
    }

    private String maskCardNumber(String number) {
        if (number == null || number.length() < 4) {
            return "****";
        }
        String lastFour = number.substring(number.length() - 4);
        return "**** **** **** " + lastFour;
    }
}