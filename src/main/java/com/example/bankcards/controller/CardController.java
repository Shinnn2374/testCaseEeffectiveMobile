package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CardResponseDto create(@RequestBody @Valid CardRequestDto dto,
                                  @AuthenticationPrincipal User user) {
        return cardService.createCard(dto, user.getUsername());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<CardResponseDto> getMyCards(@AuthenticationPrincipal User user,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return cardService.getMyCards(user.getUsername(), PageRequest.of(page, size));
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('USER')")
    public void block(@PathVariable Long id,
                      @AuthenticationPrincipal User user) {
        cardService.blockCard(id, user.getUsername());
    }
}
