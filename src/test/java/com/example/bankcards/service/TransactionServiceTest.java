package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private final Long userId = 1L;
    private Card sourceCard;
    private Card targetCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setId(userId);

        sourceCard = new Card();
        sourceCard.setId(100L);
        sourceCard.setBalance(BigDecimal.valueOf(1000));
        sourceCard.setStatus(CardStatus.ACTIVE);
        sourceCard.setUser(user);

        targetCard = new Card();
        targetCard.setId(200L);
        targetCard.setBalance(BigDecimal.valueOf(500));
        targetCard.setStatus(CardStatus.ACTIVE);
        targetCard.setUser(user);
    }

    @Test
    void transfer_shouldSucceed() {
        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(sourceCard.getId());
        request.setTargetCardId(targetCard.getId());
        request.setAmount(BigDecimal.valueOf(200));

        when(cardRepository.findById(sourceCard.getId())).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCard.getId())).thenReturn(Optional.of(targetCard));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            tx.setId(1L);
            return tx;
        });

        TransactionResponse response = transactionService.transfer(userId, request);

        assertNotNull(response);
        assertEquals(sourceCard.getId(), response.getSourceCardId());
        assertEquals(targetCard.getId(), response.getTargetCardId());
        assertEquals(BigDecimal.valueOf(200), response.getAmount());
        assertNotNull(response.getTimestamp());

        assertEquals(BigDecimal.valueOf(800), sourceCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), targetCard.getBalance());
    }

    @Test
    void transfer_shouldFail_whenSourceCardNotFound() {
        when(cardRepository.findById(100L)).thenReturn(Optional.empty());

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(EntityNotFoundException.class, () -> transactionService.transfer(userId, request));
    }

    @Test
    void transfer_shouldFail_whenTargetCardNotFound() {
        when(cardRepository.findById(100L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(200L)).thenReturn(Optional.empty());

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(EntityNotFoundException.class, () -> transactionService.transfer(userId, request));
    }

    @Test
    void transfer_shouldFail_whenSourceCardNotBelongToUser() {
        sourceCard.getUser().setId(999L); // чужой пользователь

        when(cardRepository.findById(100L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(200L)).thenReturn(Optional.of(targetCard));

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(SecurityException.class, () -> transactionService.transfer(userId, request));
    }

    @Test
    void transfer_shouldFail_whenTargetCardNotBelongToUser() {
        targetCard.getUser().setId(999L);

        when(cardRepository.findById(100L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(200L)).thenReturn(Optional.of(targetCard));

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(SecurityException.class, () -> transactionService.transfer(userId, request));
    }

    @Test
    void transfer_shouldFail_whenSourceCardInactive() {
        sourceCard.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(100L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(200L)).thenReturn(Optional.of(targetCard));

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(IllegalStateException.class, () -> transactionService.transfer(userId, request));
    }

    @Test
    void transfer_shouldFail_whenTargetCardInactive() {
        targetCard.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(100L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(200L)).thenReturn(Optional.of(targetCard));

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(IllegalStateException.class, () -> transactionService.transfer(userId, request));
    }

    @Test
    void transfer_shouldFail_whenInsufficientFunds() {
        sourceCard.setBalance(BigDecimal.valueOf(50)); // меньше чем запрошено

        when(cardRepository.findById(100L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(200L)).thenReturn(Optional.of(targetCard));

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(userId, request));
    }

    @Test
    void transfer_shouldFail_whenAmountIsZeroOrNegative() {
        when(cardRepository.findById(100L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(200L)).thenReturn(Optional.of(targetCard));

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(100L);
        request.setTargetCardId(200L);
        request.setAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(userId, request));

        request.setAmount(BigDecimal.valueOf(-10));
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(userId, request));
    }
}
