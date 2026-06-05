package com.noda.api.services;

import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.dtos.TransactionResponseDTO;
import com.noda.api.exceptions.AccountNotFoundException;
import com.noda.api.exceptions.DuplicateAccountsException;
import com.noda.api.models.Account;
import com.noda.api.models.Address;
import com.noda.api.models.Transaction;
import com.noda.api.models.User;
import com.noda.api.models.enums.AccountType;
import com.noda.api.repositories.AccountRepository;
import com.noda.api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.ThreadLocalRandom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AccountIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Nested
    @Transactional
    @DisplayName("Integration Create Account Tests")
    class createAccountTests {

        @Test
        void shouldCreateAccount() {
            User testUser = createAndSaveTestUser();
            AccountRequestDTO testRequest = new AccountRequestDTO(AccountType.CHECKING, testUser.getId());

            Account savedAccount = accountService.createAccount(testRequest);

            Assertions.assertNotNull(savedAccount.getId());
            Assertions.assertNotNull(savedAccount.getAccountNumber());
        }

        @Test
        void shouldPreventDuplicateAccountTypesForSameUser() {
            User testUser = createAndSaveTestUser();

            AccountRequestDTO checkingRequest = new AccountRequestDTO(AccountType.CHECKING, testUser.getId());

            assertNotNull(accountService.createAccount(checkingRequest));

            assertThrows(DuplicateAccountsException.class, () -> {
                accountService.createAccount(checkingRequest);
            });
        }
    }

    @Nested
    @Transactional
    @DisplayName("Integration transfers test")
    class IntegrationAccountTransfer {

        @Test
        @DisplayName("Should make a transfer when data is valid")
        void shouldMakeTransfer() {
            User sourceUser = createAndSaveTestUser();
            User targetUser = createAndSaveTestUser();

            AccountRequestDTO sourceAccountRequest = new AccountRequestDTO(AccountType.CHECKING, sourceUser.getId());
            AccountRequestDTO targetAccountRequest = new AccountRequestDTO(AccountType.CHECKING, targetUser.getId());

            Account sourceAccount = accountService.createAccount(sourceAccountRequest);
            Account targetAccount = accountService.createAccount(targetAccountRequest);

            accountService.deposit(sourceAccount.getId() , BigDecimal.valueOf(100));
            accountService.transfer(sourceAccount.getId(), targetAccount.getId(), BigDecimal.valueOf(40));

            Account updatedSource = accountRepository.findById(sourceAccount.getId())
                    .orElseThrow(() -> new AccountNotFoundException("Source account not found in database"));

            Account updatedTarget = accountRepository.findById(targetAccount.getId())
                    .orElseThrow(() -> new AccountNotFoundException("Target account not found in database"));

            Assertions.assertEquals(0, BigDecimal.valueOf(60.00).compareTo(updatedSource.getBalance()));
            Assertions.assertEquals(0, BigDecimal.valueOf(40.00).compareTo(updatedTarget.getBalance()));

        }
    }
    @Nested
    @Transactional
    @DisplayName("Statement Integration Tests")
    class IntegrationAccountStatement {

        @Test
        @DisplayName("Should return a statement")
        void shouldReturnStatement() {
            User testUser = createAndSaveTestUser();
            AccountRequestDTO testRequest = new AccountRequestDTO(AccountType.CHECKING, testUser.getId());
            Account testAccount = accountService.createAccount(testRequest);

            accountService.deposit(testAccount.getId(), BigDecimal.TEN);

            List<Transaction> testStatement = accountService.getAccountStatement(testAccount.getId());

            Assertions.assertNotNull(testStatement);
            Assertions.assertEquals(1, testStatement.size());

            Transaction responseDto =  testStatement.getFirst();

            Assertions.assertEquals("DEPOSIT", responseDto.getTransactionType().toString());


        }
    }
    private User createAndSaveTestUser() {
        Address address = new Address();
        address.setCep("93290440");
        address.setNumber("500");
        address.setComplement("Apt 302");
        address.setCity("Esteio");
        address.setNeighborhood("Centro");
        address.setState("RS");
        address.setStreet("Av. Presidente Vargas");

        User testUser = new User();
        // Generate a unique 11-digit random CPF to avoid database duplicate key errors
        long random1 = ThreadLocalRandom.current().nextLong(100000, 999999);
        long random2 = ThreadLocalRandom.current().nextLong(10000, 99999);
        String uniqueCpf = String.valueOf(random1) + String.valueOf(random2);

        // Use the current timestamp in milliseconds to ensure a unique email per test execution
        long timestamp = System.currentTimeMillis();
        testUser.setName("Test User");
        testUser.setCpf(uniqueCpf);
        testUser.setEmail("testuser" + timestamp + "@gmail.com");
        testUser.setPassword("UserStrongPassword@123");
        testUser.setBirthday(LocalDate.of(1998, 6, 20));
        testUser.setAddress(address);

        return userRepository.save(testUser);
    }
}