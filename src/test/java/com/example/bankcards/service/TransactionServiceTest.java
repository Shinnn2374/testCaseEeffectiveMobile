package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.dto.TransactionResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Card createCard(Long id, Long userId, BigDecimal balance, CardStatus status) {
        Card card = new Card();
        card.setId(id);
        card.setBalance(balance);
        card.setStatus(status);
        User user = new User();
        user.setId(userId);
        card.setUser(user);
        return card;
    }

    @Test
    void transfer_shouldTransferSuccessfully() {
        Long userId = 1L;
        Long sourceCardId = 100L;
        Long targetCardId = 200L;

        Card sourceCard = createCard(sourceCardId, userId, BigDecimal.valueOf(200), CardStatus.ACTIVE);
        Card targetCard = createCard(targetCardId, userId, BigDecimal.valueOf(100), CardStatus.ACTIVE);

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(sourceCardId);
        request.setTargetCardId(targetCardId);
        request.setAmount(BigDecimal.valueOf(50));

        when(cardRepository.findById(sourceCardId)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCardId)).thenReturn(Optional.of(targetCard));
        when(transactionRepository.save(any())).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            t.setTimestamp(LocalDateTime.now());
            return t;
        });

        TransactionResponse response = transactionService.transfer(userId, request);

        assertEquals(BigDecimal.valueOf(150), sourceCard.getBalance());
        assertEquals(BigDecimal.valueOf(150), targetCard.getBalance());
        assertEquals(1L, response.getId());
        assertEquals(sourceCardId, response.getSourceCardId());
        assertEquals(targetCardId, response.getTargetCardId());
        assertEquals(BigDecimal.valueOf(50), response.getAmount());
    }

    @Test
    void transfer_shouldThrowIfSourceCardNotFound() {
        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(1L);
        request.setTargetCardId(2L);
        request.setAmount(BigDecimal.TEN);

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.transfer(1L, request));
    }

    @Test
    void transfer_shouldThrowIfTargetCardNotFound() {
        Card sourceCard = createCard(1L, 1L, BigDecimal.valueOf(100), CardStatus.ACTIVE);

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(1L);
        request.setTargetCardId(2L);
        request.setAmount(BigDecimal.TEN);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.transfer(1L, request));
    }

    @Test
    void transfer_shouldThrowIfCardsNotBelongToUser() {
        Card sourceCard = createCard(1L, 1L, BigDecimal.valueOf(100), CardStatus.ACTIVE);
        Card targetCard = createCard(2L, 2L, BigDecimal.valueOf(100), CardStatus.ACTIVE); // другой пользователь

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(1L);
        request.setTargetCardId(2L);
        request.setAmount(BigDecimal.TEN);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(targetCard));

        assertThrows(SecurityException.class, () -> transactionService.transfer(1L, request));
    }

    @Test
    void transfer_shouldThrowIfInsufficientFunds() {
        Card sourceCard = createCard(1L, 1L, BigDecimal.valueOf(5), CardStatus.ACTIVE);
        Card targetCard = createCard(2L, 1L, BigDecimal.valueOf(100), CardStatus.ACTIVE);

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(1L);
        request.setTargetCardId(2L);
        request.setAmount(BigDecimal.TEN);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(targetCard));

        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(1L, request));
    }

    @Test
    void transfer_shouldThrowIfAmountIsNonPositive() {
        Card sourceCard = createCard(1L, 1L, BigDecimal.valueOf(100), CardStatus.ACTIVE);
        Card targetCard = createCard(2L, 1L, BigDecimal.valueOf(100), CardStatus.ACTIVE);

        TransactionRequest request = new TransactionRequest();
        request.setSourceCardId(1L);
        request.setTargetCardId(2L);
        request.setAmount(BigDecimal.ZERO);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(targetCard));

        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(1L, request));
    }
}
