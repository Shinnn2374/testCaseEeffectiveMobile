package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    @Autowired
    private TransferService transferService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequestDto dto,
                                           @AuthenticationPrincipal User user) {
        transferService.transfer(user.getUsername(), dto);
        return ResponseEntity.ok("Transfer completed");
    }
}
