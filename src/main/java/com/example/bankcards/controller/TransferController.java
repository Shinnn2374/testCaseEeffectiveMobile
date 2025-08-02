package com.example.bankcards.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("@cardSecurity.isCardOwner(#request.sourceCardId)")
    public ResponseEntity<TransferResponse> transfer(
            @RequestBody TransferRequest request
    ) {
        return ResponseEntity.ok(transferService.transfer(request));
    }
}