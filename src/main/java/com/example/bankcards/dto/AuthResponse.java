package com.example.bankcards.dto;

import lombok.Data;

@Data
public class AuthResponse {

    public String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
