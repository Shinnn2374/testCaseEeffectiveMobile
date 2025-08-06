package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void transfer_Success() {
        // Подготовка данных
        User user = new User();
        user.setId(1L);

        Card sourceCard = new Card();
        sourceCard.setId(1L);
        sourceCard.setUser(user);
        sourceCard.setBalance(BigDecimal.valueOf(1000));
        sourceCard.setStatus(CardStatus.ACTIVE);

        Card targetCard = new Card();
        targetCard.setId(2L);
        targetCard.setUser(user);
        targetCard.setBalance(BigDecimal.valueOf(500));
        targetCard.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(targetCard));

        TransactionRequest request = new TransactionRequest(1L, 2L, BigDecimal.valueOf(200));
        TransactionResponse response = transactionService.transfer(1L, request);

        assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(200));
        verify(cardRepository, times(2)).findById(any());
        verify(transactionRepository).save(any());
    }
}