package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceCard_IdOrTargetCard_Id(Long sourceCardId, Long targetCardId);
}