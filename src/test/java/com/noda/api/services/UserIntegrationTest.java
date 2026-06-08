package com.noda.api.services;

import com.noda.api.dtos.AddressRequestDTO;
import com.noda.api.dtos.UserRequestDTO;
import com.noda.api.dtos.ViaCepResponseDTO;
import com.noda.api.models.User;
import com.noda.api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockitoBean
    private ViaCepService viaCepService;

    @Nested
    @Transactional
    @DisplayName("Integration Create User tests")
    class createUserTest{

        @Test
        void shouldCreateUser() {

            long random1 = ThreadLocalRandom.current().nextLong(100000, 999999);
            long random2 = ThreadLocalRandom.current().nextLong(10000, 99999);
            String uniqueCpf = String.valueOf(random1) + String.valueOf(random2);
            long timestamp = System.currentTimeMillis();

            ViaCepResponseDTO fakeAddress = new ViaCepResponseDTO(
                    "Av. Presidente Vargas",
                    "Centro",
                    "Esteio",
                    "RS",
                    null
            );

            Mockito.when(viaCepService.fetchAddressByCep("666666666")).thenReturn(fakeAddress);

            AddressRequestDTO testAddress = new AddressRequestDTO("666666666", "187", null);
            UserRequestDTO testUserRequest = new UserRequestDTO(
                    "Test User",
                    uniqueCpf,
                    "testuser" + timestamp + "@gmail.com",
                    "UserStrongPassword@123",
                    LocalDate.of(1998, 6, 20),
                    testAddress
            );

            User result = userService.save(testUserRequest);

            Assertions.assertNotNull(result.getId());
            Assertions.assertEquals("Test User", result.getName());
        }
    }
}