package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CardMapper cardMapper;

    public CardResponseDto createCard(CardRequestDto dto, String ownerUsername) {
        User owner = userRepo.findByUsername(ownerUsername).orElseThrow();

        Card card = new Card();
        card.setEncryptedNumber(encrypt(dto.getCardNumber()));
        card.setExpiryDate(dto.getExpiryDate());
        card.setOwner(owner);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        cardRepo.save(card);
        return cardMapper.toDto(card);
    }

    public List<CardResponseDto> getMyCards(String username, Pageable pageable) {
        return cardRepo.findByOwnerUsername(username, pageable)
                .stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
    }

    public void blockCard(Long cardId, String requester) {
        Card card = cardRepo.findById(cardId).orElseThrow();
        if (!card.getOwner().getUsername().equals(requester)) {
            throw new AccessDeniedException("Not your card");
        }
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    private String encrypt(String number) {
        return number;
    }
}
