package com.example.bankcards.util;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "maskedNumber", expression = "java(maskCardNumber(card.getCardNumber()))")
    CardResponse toResponse(Card card);

    default String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    Card toEntity(CardRequest request);
}