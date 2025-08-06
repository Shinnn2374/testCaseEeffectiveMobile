package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilterRequest;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public Page<CardResponse> getFilteredCards(Long userId, CardFilterRequest filter, Pageable pageable) {
        Specification<Card> spec = Specification.where(CardSpecification.byUserId(userId));

        if (filter.getStatus() != null) {
            spec = spec.and(CardSpecification.byStatus(filter.getStatus()));
        }
        if (filter.getOwnerName() != null) {
            spec = spec.and(CardSpecification.byOwnerName(filter.getOwnerName()));
        }
        if (filter.getExpirationDateFrom() != null) {
            spec = spec.and(CardSpecification.byExpirationDateFrom(filter.getExpirationDateFrom()));
        }
        if (filter.getExpirationDateTo() != null) {
            spec = spec.and(CardSpecification.byExpirationDateTo(filter.getExpirationDateTo()));
        }

        return cardRepository.findAll(spec, pageable).map(this::mapToCardResponse);
    }

    @Transactional
    public CardResponse createCard(CardRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        Card card = new Card();
        card.setNumber(generateCardNumber());
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
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (!card.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
        return mapToCardResponse(card);
    }

    private String generateCardNumber() {
        return String.format("%016d", (long) (Math.random() * 1_0000_0000_0000_0000L));
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

    private String maskCardNumber(String number) {
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}