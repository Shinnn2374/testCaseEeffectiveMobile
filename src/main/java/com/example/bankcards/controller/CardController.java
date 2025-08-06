package com.example.bankcards.controller;

import com.example.bankcards.dto.CardFilterRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping("/filter")
    public ResponseEntity<Page<CardResponse>> getFilteredCards(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CardFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("expirationDate").ascending());
        Page<CardResponse> cards = cardService.getFilteredCards(userId, filter, pageable);
        return ResponseEntity.ok(cards);
    }
}