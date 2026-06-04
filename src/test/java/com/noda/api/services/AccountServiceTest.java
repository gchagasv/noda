package com.noda.api.services;

import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.dtos.TransactionResponseDTO;
import com.noda.api.exceptions.*;
import com.noda.api.models.Account;
import com.noda.api.models.Transaction;
import com.noda.api.models.User;
import com.noda.api.models.enums.AccountType;
import com.noda.api.models.enums.TransactionType;
import com.noda.api.repositories.AccountRepository;
import com.noda.api.repositories.TransactionRepository;
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
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

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

    @Nested
    @DisplayName("Transfer Tests")
    class TransferTests {

        @Test
        @DisplayName("Should make a transfer successfully when data is valid")
        void shouldMakeTransfer() {

            Account sourceAccount = new Account();
            sourceAccount.setId(666L);
            sourceAccount.setBalance(BigDecimal.valueOf(100));

            Account targetAccount = new Account();
            targetAccount.setId(999L);

            BigDecimal testAmount = BigDecimal.valueOf(25.50);

            Mockito.when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            Mockito.when(accountRepository.findById(targetAccount.getId())).thenReturn(Optional.of(targetAccount));

            accountService.transfer(sourceAccount.getId(), targetAccount.getId(), testAmount);

            Mockito.verify(transactionRepository, Mockito.times(1)).save(Mockito.any());

            Assertions.assertEquals(BigDecimal.valueOf(74.50), sourceAccount.getBalance());
            Assertions.assertEquals(BigDecimal.valueOf(25.50), targetAccount.getBalance());
        }



        @Test
        @DisplayName("Should throw SameAccountTransferException when account IDs are the same")
        void shouldThrowExceptionWhenEqualsId() {

        Account testAccount = new Account();
        testAccount.setId(1L);
        BigDecimal testAmount = BigDecimal.valueOf(25.50);

        Assertions.assertThrows(SameAccountTransferException.class, () -> {
            accountService.transfer(testAccount.getId(), testAccount.getId(), testAmount);

        });
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when source account does not exist")
        void shouldThrowExceptionWhenSourceAccountNotFound() {

            Account sourceAccount = new Account();
            sourceAccount.setId(666L);

            Account testAccount1 = new Account();
            testAccount1.setId(999L);
            BigDecimal testAmount = BigDecimal.valueOf(25.50);

            Mockito.when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(AccountNotFoundException.class, () -> {
                accountService.transfer(sourceAccount.getId(), testAccount1.getId(), testAmount );
            });
        }

        @Test
        @DisplayName("Should throw InsufficientFundsException when source account has insufficient balance")
        void shouldThrowExceptionWhenSourceAccountHasNoEnoughBalance() {

            Account sourceAccount = new Account();
            sourceAccount.setId(666L);

            Account targetAccount = new Account();
            targetAccount.setId(999L);
            BigDecimal testAmount = BigDecimal.valueOf(25.50);

            Mockito.when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            Mockito.when(accountRepository.findById(targetAccount.getId())).thenReturn(Optional.of(targetAccount));

            Assertions.assertThrows(InsufficientFundsException.class, () -> {
                accountService.transfer(sourceAccount.getId(), targetAccount.getId(), testAmount);
            });
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when target account does not exist")
        void shouldThrowExceptionWhenTargetAccountNotFound() {

            Account sourceAccount = new Account();
            sourceAccount.setId(666L);

            Account targetAccount = new Account();
            targetAccount.setId(999L);
            BigDecimal testAmount = BigDecimal.valueOf(25.50);

            Mockito.when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
            Mockito.when(accountRepository.findById(targetAccount.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(AccountNotFoundException.class, () -> {
                accountService.transfer(sourceAccount.getId(), targetAccount.getId(), testAmount );
            });

        }
    }
    @Nested
    @DisplayName("Deposit Tests")
    class DepositTests {

        @Test
        @DisplayName("Should make a deposit if data is valid")
        void shouldMakeDeposit() {

            Account testAccount = new Account();
            testAccount.setId(666L);
            BigDecimal testAmount = BigDecimal.TEN;

            Mockito.when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

            accountService.deposit(testAccount.getId(), testAmount);

            Mockito.verify(transactionRepository, Mockito.times(1)).save(Mockito.any());
            Assertions.assertEquals(BigDecimal.TEN, testAccount.getBalance());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when amount is zero or less")
        void shouldThrowExceptionWhenDepositWithNoAmount() {

            Account testAccount = new Account();
            testAccount.setId(666L);
            BigDecimal testAmount = BigDecimal.ZERO;

            Mockito.when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                accountService.deposit(testAccount.getId(), testAmount);
            });

        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when account to be deposited does not exist")
        void shouldThrowExceptionWhenAccountNotFound() {

            Account testAccount = new Account();
            testAccount.setId(666L);
            BigDecimal testAmount = BigDecimal.ONE;

            Mockito.when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(AccountNotFoundException.class, () -> {
                accountService.deposit(testAccount.getId(), testAmount);
            });
         }
    }
    @Nested
    @DisplayName("Withdrawal Tests")
    class WithdrawTests {

       @Test
       @DisplayName("Should successfully withdraw when data is valid")
       void shouldMakeWithdraw() {
           Account testAccount = new Account();
           testAccount.setId(666L);
           testAccount.setBalance(BigDecimal.TEN);
           BigDecimal testAmount = BigDecimal.TEN;

           Mockito.when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

           accountService.withdrawal(testAccount.getId(), testAmount);

           Mockito.verify(transactionRepository, Mockito.times(1)).save(Mockito.any());
           Assertions.assertEquals(BigDecimal.ZERO, testAccount.getBalance());
       }

        @Test
        @DisplayName("Should throw AccountNotFoundException when account does not exist")
        void shouldThrowExceptionWhenAccountNotFound() {

            Account testAccount = new Account();
            testAccount.setId(666L);
            BigDecimal testAmount = BigDecimal.ONE;

            Mockito.when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(AccountNotFoundException.class, () -> {
                accountService.withdrawal(testAccount.getId(), testAmount);
            });
        }

       @Test
       @DisplayName("Should throw IllegalArgumentException when amount is zero or less")
       void shouldThrowExceptionWhenWithdrawWithNoAmount() {

           Account testAccount = new Account();
           testAccount.setId(666L);
           BigDecimal testAmount = BigDecimal.ZERO;

           Mockito.when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

           Assertions.assertThrows(IllegalArgumentException.class, () -> {
               accountService.withdrawal(testAccount.getId(), testAmount);
           });
       }

       @Test
       @DisplayName("Should throw InsufficientFundsException when account has insufficient balance")
        void shouldThrowExceptionWhenAccountHasNoEnoughBalance() {
           Account testAccount = new Account();
           testAccount.setId(666L);
           testAccount.setBalance(BigDecimal.TEN);
           BigDecimal testAmount = BigDecimal.valueOf(90);

           Mockito.when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

           Assertions.assertThrows(InsufficientFundsException.class, () -> {
               accountService.withdrawal(testAccount.getId(), testAmount);
           });
       }
    }
    @Nested
    @DisplayName("Account Statement Tests")
    class AccountStatementTests {

        @Test
        @DisplayName("Should throw AccountNotFoundException when account does not exist")
        void shouldThrowExceptionWhenAccountNotFound() {
            Account testAccount = new Account();
            testAccount.setId(666L);

            Mockito.when(accountRepository.existsById(testAccount.getId())).thenReturn(false);

            Assertions.assertThrows(AccountNotFoundException.class, () -> {
                accountService.getAccountStatement(testAccount.getId());
            });
        }
        @Test
        @DisplayName("Should return account statement when data is valid")
        void shouldReturnStatementSuccessfully() {
            Account testAccount = new Account();
            testAccount.setId(666L);

            Transaction mockTransaction = new Transaction();
            mockTransaction.setId(1L);
            mockTransaction.setAmount(BigDecimal.valueOf(100));
            mockTransaction.setTimestamp(java.time.LocalDateTime.now());
            mockTransaction.setTransactionType(TransactionType.DEPOSIT);

            Mockito.when(accountRepository.existsById(testAccount.getId())).thenReturn(true);
            Mockito.when(transactionRepository.findBySourceAccountIdOrDestinationAccountId(testAccount.getId(), testAccount.getId()))
                            .thenReturn(List.of(mockTransaction));


            List<TransactionResponseDTO> result = accountService.getAccountStatement(testAccount.getId());

           Assertions.assertNotNull(result);
           Assertions.assertEquals(1, result.size());

            TransactionResponseDTO responseDto = result.getFirst();
            Assertions.assertEquals(mockTransaction.getId(), responseDto.transactionId());
            Assertions.assertEquals(mockTransaction.getAmount(), responseDto.amount());
            Assertions.assertEquals("DEPOSIT", responseDto.transactionType());
        }
    }
}
