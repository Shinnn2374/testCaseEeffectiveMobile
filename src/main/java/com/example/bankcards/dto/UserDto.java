package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private UserRole role;
}