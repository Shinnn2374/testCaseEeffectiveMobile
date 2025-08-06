package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilterRequest;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCard_shouldCreateCardSuccessfully() {
        Long userId = 1L;
        CardRequest request = new CardRequest();
        request.setOwnerName("John Doe");
        request.setExpirationDate(LocalDate.now().plusYears(2));
        request.setBalance(BigDecimal.valueOf(100));

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardResponse response = cardService.createCard(request, userId);

        assertNotNull(response);
        assertEquals("John Doe", response.getOwnerName());
        assertEquals(CardStatus.ACTIVE, response.getStatus());
        assertTrue(response.getMaskedNumber().startsWith("**** **** **** "));
    }

    @Test
    void createCard_shouldThrowExceptionForNegativeBalance() {
        Long userId = 1L;

        CardRequest request = new CardRequest();
        request.setOwnerName("John Doe");
        request.setExpirationDate(LocalDate.now().plusYears(2));
        request.setBalance(BigDecimal.valueOf(-50));

        User mockUser = new User();
        mockUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class, () -> cardService.createCard(request, userId));
    }


    @Test
    void createCard_shouldThrowIfUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CardRequest request = new CardRequest();
        request.setOwnerName("John Doe");
        request.setExpirationDate(LocalDate.now().plusYears(1));
        request.setBalance(BigDecimal.valueOf(100));

        assertThrows(EntityNotFoundException.class, () -> cardService.createCard(request, userId));
    }


    @Test
    void getCardById_shouldReturnCardIfOwnerMatches() {
        Long cardId = 10L;
        Long userId = 5L;

        User user = new User();
        user.setId(userId);

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("1234567890123456");
        card.setOwnerName("John Doe");
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.TEN);
        card.setExpirationDate(LocalDate.now().plusYears(1));
        card.setUser(user);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardResponse response = cardService.getCardById(cardId, userId);

        assertNotNull(response);
        assertEquals("John Doe", response.getOwnerName());
    }

    @Test
    void getCardById_shouldThrowIfNotOwner() {
        Long cardId = 10L;
        Long userId = 1L;
        Long otherUserId = 2L;

        User user = new User();
        user.setId(otherUserId);

        Card card = new Card();
        card.setId(cardId);
        card.setUser(user);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(SecurityException.class, () -> cardService.getCardById(cardId, userId));
    }

    @Test
    void getCardById_shouldThrowIfCardNotFound() {
        Long cardId = 123L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.getCardById(cardId, 1L));
    }

    @Test
    void getFilteredCards_shouldReturnPage() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        CardFilterRequest filter = new CardFilterRequest();

        Card card = new Card();
        card.setId(1L);
        card.setNumber("1234567890123456");
        card.setOwnerName("John");
        card.setStatus(CardStatus.ACTIVE);
        card.setExpirationDate(LocalDate.now().plusYears(1));
        card.setBalance(BigDecimal.valueOf(100));
        card.setUser(new User());

        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);

        Page<CardResponse> result = cardService.getFilteredCards(userId, filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getOwnerName());
    }

}
