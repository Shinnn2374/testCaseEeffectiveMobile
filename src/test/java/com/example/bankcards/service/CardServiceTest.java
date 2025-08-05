package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_shouldSaveCardAndReturnDto() {
        String username = "user1";
        CardRequestDto dto = new CardRequestDto();
        dto.setCardNumber("1234567812345678");
        dto.setExpiryDate(LocalDate.now().plusYears(1));

        User user = new User();
        user.setUsername(username);

        Card savedCard = new Card();
        savedCard.setId(1L);
        savedCard.setOwner(user);
        savedCard.setEncryptedNumber("encrypted");
        savedCard.setBalance(BigDecimal.ZERO);

        CardResponseDto expectedDto = new CardResponseDto();
        expectedDto.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cardRepository.save(any())).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(expectedDto);

        CardResponseDto result = cardService.createCard(dto, username);

        assertEquals(1L, result.getId());
        verify(cardRepository).save(any());
    }
}
