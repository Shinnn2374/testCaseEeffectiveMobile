package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByOwnerUsername(String username, Pageable pageable);
    Page<Card> findByOwnerUsernameAndStatus(String username, CardStatus status, Pageable pageable);
    Page<Card> findByStatus(CardStatus status, Pageable pageable);
}
