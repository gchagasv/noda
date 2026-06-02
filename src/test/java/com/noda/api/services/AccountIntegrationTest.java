package com.noda.api.services;

import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.exceptions.DuplicateAccountsException;
import com.noda.api.models.Address;
import com.noda.api.models.User;
import com.noda.api.models.enums.AccountType;
import com.noda.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AccountIntegrationTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPreventDuplicateAccountTypesForSameUser() {
        User testUser = createAndSaveTestUser();

        AccountRequestDTO checkingRequest = new AccountRequestDTO(AccountType.CHECKING, testUser.getId());

        assertNotNull(accountService.createAccount(checkingRequest));

        assertThrows(DuplicateAccountsException.class, () -> {
            accountService.createAccount(checkingRequest);
        });
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
        long timestamp = System.currentTimeMillis();
        testUser.setName("Test User");
        testUser.setCpf(String.valueOf(timestamp).substring(0, 11));
        testUser.setEmail("testuser" + timestamp + "@gmail.com");
        testUser.setPassword("UserStrongPassword@123");
        testUser.setBirthday(LocalDate.of(1998, 6, 20));
        testUser.setAddress(address);

        return userRepository.save(testUser);
    }
}
