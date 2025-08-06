package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class TransferException extends BusinessException {
    public TransferException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}