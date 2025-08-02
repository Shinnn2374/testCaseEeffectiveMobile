package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Transactional
    public CardResponse createCard(CardRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        Card card = cardMapper.toEntity(request);
        card.setUser(user);
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);

        return cardMapper.toResponse(card);
    }

    public Page<CardResponse> getUserCards(Pageable pageable, String statusFilter) {
        return cardRepository.findByUserAndStatus(
                SecurityUtils.getCurrentUser(),
                CardStatus.fromString(statusFilter),
                pageable
        ).map(cardMapper::toResponse);
    }

    @Transactional
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        card.setStatus(CardStatus.BLOCKED);
    }
}