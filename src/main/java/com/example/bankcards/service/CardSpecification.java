package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class CardSpecification {
    public static Specification<Card> byUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Card> byStatus(CardStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Card> byOwnerName(String ownerName) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("ownerName")),
                "%" + ownerName.toLowerCase() + "%"
        );
    }

    public static Specification<Card> byExpirationDateFrom(LocalDate date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expirationDate"), date);
    }

    public static Specification<Card> byExpirationDateTo(LocalDate date) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("expirationDate"), date);
    }
}