package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid TransactionRequest request
    ) {
        TransactionResponse response = transactionService.transfer(userId, request);
        return ResponseEntity.ok(response);
    }
}