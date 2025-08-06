package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilterRequest;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.CardNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @Test
    void createCard_shouldCreateSuccessfully() {
        Long userId = 1L;

        CardRequest request = new CardRequest();
        request.setOwnerName("John Doe");
        request.setExpirationDate(LocalDate.now().plusYears(2));
        request.setBalance(BigDecimal.valueOf(100));

        User user = createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CardResponse response = cardService.createCard(request, userId);

        assertNotNull(response);
        assertEquals("John Doe", response.getOwnerName());
        assertEquals(BigDecimal.valueOf(100), response.getBalance());
    }

    @Test
    void createCard_shouldThrowIfUserNotFound() {
        Long userId = 99L;

        CardRequest request = new CardRequest();
        request.setOwnerName("John");
        request.setExpirationDate(LocalDate.now().plusYears(2));
        request.setBalance(BigDecimal.valueOf(100));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.createCard(request, userId));
    }

    @Test
    void createCard_shouldThrowIfBalanceNegative() {
        Long userId = 1L;

        CardRequest request = new CardRequest();
        request.setOwnerName("John");
        request.setExpirationDate(LocalDate.now().plusYears(2));
        request.setBalance(BigDecimal.valueOf(-50));

        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser(userId)));

        assertThrows(IllegalArgumentException.class, () -> cardService.createCard(request, userId));
    }

    @Test
    void getCardById_shouldReturnCard() {
        Long userId = 1L;
        Long cardId = 1L;

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("1234567890123456");
        card.setOwnerName("John");
        card.setExpirationDate(LocalDate.now().plusYears(1));
        card.setBalance(BigDecimal.valueOf(100));
        card.setStatus(CardStatus.ACTIVE);

        User user = createUser(userId);
        card.setUser(user);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardResponse response = cardService.getCardById(cardId, userId);

        assertNotNull(response);
        assertEquals("John", response.getOwnerName());
        assertEquals("**** **** **** 3456", response.getMaskedNumber());
    }

    @Test
    void getCardById_shouldThrowIfCardNotFound() {
        Long userId = 1L;
        Long cardId = 99L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(cardId, userId));
    }

    @Test
    void getCardById_shouldThrowIfAccessDenied() {
        Long cardId = 1L;

        Card card = new Card();
        card.setId(cardId);
        card.setUser(createUser(999L)); // чужой пользователь

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(BusinessException.class, () -> cardService.getCardById(cardId, 1L));
    }

    @Test
    void getFilteredCards_shouldReturnPage() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        CardFilterRequest filter = new CardFilterRequest(); // пустой фильтр

        Card card = new Card();
        card.setId(1L);
        card.setNumber("1234567890123456");
        card.setOwnerName("John");
        card.setStatus(CardStatus.ACTIVE);
        card.setExpirationDate(LocalDate.now().plusYears(1));
        card.setBalance(BigDecimal.valueOf(100));
        card.setUser(createUser(userId));

        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);

        Page<CardResponse> result = cardService.getFilteredCards(userId, filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getOwnerName());
    }
}
