package com.example.bankcards.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardResponse {
    private Long id;
    private String maskedNumber; // "**** **** **** 1234"
    private String ownerName;
    private String status;
    private double balance;
}