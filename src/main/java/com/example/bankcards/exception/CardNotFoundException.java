package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class CardNotFoundException extends BusinessException {
    public CardNotFoundException(Long id) {
        super("Card not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}