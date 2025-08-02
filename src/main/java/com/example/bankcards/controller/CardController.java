package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // Создание карты (только ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> createCard(@RequestBody CardRequest request) {
        return ResponseEntity.ok(cardService.createCard(request));
    }

    // Получение карт пользователя (с пагинацией)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<CardResponse>> getUserCards(
            Pageable pageable,
            @RequestParam(required = false) String statusFilter
    ) {
        return ResponseEntity.ok(cardService.getUserCards(pageable, statusFilter));
    }

    // Блокировка карты (ADMIN или владелец)
    @PutMapping("/{cardId}/block")
    @PreAuthorize("@cardSecurity.isCardOwner(#cardId) or hasRole('ADMIN')")
    public ResponseEntity<Void> blockCard(@PathVariable Long cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.noContent().build();
    }

    // Удаление карты (только ADMIN)
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}