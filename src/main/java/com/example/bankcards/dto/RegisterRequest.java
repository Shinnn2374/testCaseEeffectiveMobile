package com.example.bankcards.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    public String username;

    public String password;

    public String role;
}
