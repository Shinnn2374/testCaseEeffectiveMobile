package com.example.bankcards.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    public String username;

    public String password;

    public String role;
}
