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
import com.example.bankcards.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired private EncryptionUtil encryptionUtil;

    public CardResponseDto createCard(CardRequestDto dto, String ownerUsername) {
        User owner = userRepo.findByUsername(ownerUsername).orElseThrow();

        Card card = new Card();
        card.setEncryptedNumber(encryptionUtil.encrypt(dto.getCardNumber()));
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

    public List<CardResponseDto> getUserCards(String username, String statusStr, Pageable pageable) {
        Page<Card> page;

        if (statusStr == null) {
            page = cardRepo.findByOwnerUsername(username, pageable);
        } else {
            CardStatus status = CardStatus.valueOf(statusStr.toUpperCase());
            page = cardRepo.findByOwnerUsernameAndStatus(username, status, pageable);
        }

        return page.stream().map(cardMapper::toDto).toList();
    }

    public List<CardResponseDto> getAllCardsForAdmin(String statusStr, Pageable pageable) {
        Page<Card> page;

        if (statusStr == null) {
            page = cardRepo.findAll(pageable);
        } else {
            CardStatus status = CardStatus.valueOf(statusStr.toUpperCase());
            page = cardRepo.findByStatus(status, pageable);
        }

        return page.stream().map(cardMapper::toDto).toList();
    }
}
