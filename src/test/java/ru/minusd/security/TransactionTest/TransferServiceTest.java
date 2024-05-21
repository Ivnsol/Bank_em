package ru.minusd.security.TransactionTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.minusd.security.domain.model.BankAccount;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.repository.BankAccountRepository;
import ru.minusd.security.repository.UserRepository;
import ru.minusd.security.service.TransferService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private TransferService transferService;

    private User fromUser;
    private User toUser;

    @BeforeEach
    public void setUp() {
        fromUser = new User();
        fromUser.setId(1L);
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setBalance(BigDecimal.valueOf(1000));
        bankAccount1.setInitialDeposit(BigDecimal.valueOf(1000));
        fromUser.setBankAccount(bankAccount1);

        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setBalance(BigDecimal.valueOf(500));
        bankAccount2.setInitialDeposit(BigDecimal.valueOf(500));
        toUser = new User();
        toUser.setId(2L);
        toUser.setBankAccount(bankAccount2);
    }

    @Test
    public void testTransferSufficientFunds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        BigDecimal amount = BigDecimal.valueOf(200);

        transferService.transfer(1L, 2L, amount);

        assertEquals(BigDecimal.valueOf(800), fromUser.getBankAccount().getBalance());
        assertEquals(BigDecimal.valueOf(700), toUser.getBankAccount().getBalance());

        verify(bankAccountRepository, times(2)).save(any(BankAccount.class));
    }

    @Test
    public void testTransferInsufficientFunds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        BigDecimal amount = BigDecimal.valueOf(1200);

        assertThrows(IllegalArgumentException.class, () -> {
            transferService.transfer(1L, 2L, amount);
        });

        assertEquals(BigDecimal.valueOf(1000), fromUser.getBankAccount().getBalance());
        assertEquals(BigDecimal.valueOf(500), toUser.getBankAccount().getBalance());

        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    public void testTransferUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));

        BigDecimal amount = BigDecimal.valueOf(200);

        assertThrows(IllegalArgumentException.class, () -> {
            transferService.transfer(1L, 2L, amount);
        });

        assertEquals(BigDecimal.valueOf(1000), fromUser.getBankAccount().getBalance());
        assertEquals(BigDecimal.valueOf(500), toUser.getBankAccount().getBalance());

        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }
}
