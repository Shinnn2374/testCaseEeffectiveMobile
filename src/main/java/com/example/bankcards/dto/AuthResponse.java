package com.example.bankcards.dto;

import lombok.Builder;

@Builder
public class AuthResponse {
    private String token;
    private Long expiresIn;
    private String email;
    private String name;
    private String role;

    public AuthResponse(String token, Long expiresIn, String email, String name, String role) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public AuthResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}