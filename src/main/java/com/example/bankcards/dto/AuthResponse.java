package com.example.bankcards.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

    public String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
