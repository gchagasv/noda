package com.noda.api.services;

import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.exceptions.DuplicateAccountsException;
import com.noda.api.exceptions.UserNotFoundException;
import com.noda.api.models.Account;
import com.noda.api.models.User;
import com.noda.api.models.enums.AccountType;
import com.noda.api.repositories.AccountRepository;
import com.noda.api.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @Nested
    @DisplayName("Create Account Tests")
    class CreateAccountTests {

        @Test
        @DisplayName("Should successfully create an account when data is valid")
        void shouldCreateAccountSuccessfully() {
             // arrange
            Long userId = 1L;
            AccountRequestDTO dto = new AccountRequestDTO(AccountType.CHECKING, userId);

            User mockUser = new User();
            mockUser.setId(userId);

            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            Mockito.when(accountRepository.existsByUserIdAndAccountType(userId, dto.accountType())).thenReturn(false);

            // act
            accountService.createAccount(dto);

            //  assert
            Mockito.verify(accountRepository, Mockito.times(1)).save(accountCaptor.capture());
            Account savedAccount = accountCaptor.getValue();

            Assertions.assertNotNull(savedAccount.getAccountNumber());
            Assertions.assertEquals(8, savedAccount.getAccountNumber().length());
            Assertions.assertEquals(BigDecimal.ZERO, savedAccount.getBalance());
            Assertions.assertEquals(mockUser, savedAccount.getUser());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotFound() {

            Long invalidUserId = 999L;
            AccountRequestDTO dto = new AccountRequestDTO(AccountType.CHECKING, invalidUserId);

            Mockito.when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());


            Assertions.assertThrows(UserNotFoundException.class, () -> {
                accountService.createAccount(dto);
            });

            Mockito.verify(accountRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Should throw DuplicateAccountsException when account type already exists for user")
        void shouldThrowExceptionWhenAccountTypeDuplicate() {

            Long userId = 1L;
            AccountRequestDTO dto = new AccountRequestDTO(AccountType.CHECKING, userId);

            User mockUser = new User();
            mockUser.setId(userId);

            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            Mockito.when(accountRepository.existsByUserIdAndAccountType(userId, dto.accountType())).thenReturn(true);


            Assertions.assertThrows(DuplicateAccountsException.class, () -> {
                accountService.createAccount(dto);
            });

            Mockito.verify(accountRepository, Mockito.never()).save(Mockito.any());
        }

    }
}