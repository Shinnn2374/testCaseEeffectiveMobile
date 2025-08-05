package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transfer_shouldMoveMoney() {
        String username = "user1";

        Card from = new Card();
        from.setId(1L);
        from.setBalance(new BigDecimal("200.00"));
        from.setStatus(CardStatus.ACTIVE);
        from.setOwner(new User());
        from.getOwner().setUsername(username);

        Card to = new Card();
        to.setId(2L);
        to.setBalance(new BigDecimal("100.00"));
        to.setStatus(CardStatus.ACTIVE);
        to.setOwner(from.getOwner());

        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardId(1L);
        dto.setToCardId(2L);
        dto.setAmount(new BigDecimal("50.00"));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        transferService.transfer(username, dto);

        assertEquals(new BigDecimal("150.00"), from.getBalance());
        assertEquals(new BigDecimal("150.00"), to.getBalance());
    }
}
