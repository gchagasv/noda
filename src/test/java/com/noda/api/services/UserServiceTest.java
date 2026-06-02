package com.noda.api.services;


import com.noda.api.dtos.*;
import com.noda.api.models.User;
import com.noda.api.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private ViaCepService viaCepService;

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {


        @Test
        @DisplayName("Should successfully create an user when data is valid")
        void shouldCreateUserSuccessfully () {

        String mockUserCep = "99888777";
        String mockUserNumber = "26";
        AddressRequestDTO addressDTO = new AddressRequestDTO(mockUserCep, mockUserNumber, null);

        UserRequestDTO dto = new UserRequestDTO("eren", "11122233344", "test@gmail.com",
                "senha123", LocalDate.of(2006, 6, 3), addressDTO);

        User mockUser = new User();
        mockUser.setName("eren");
        mockUser.setCpf("11122233344");
        mockUser.setEmail("test@gmail.com");

        ViaCepResponseDTO viaCepResponse = new ViaCepResponseDTO(
                    "Av. Presidente Vargas",
                    "Centro",
                    "Esteio",
                    "RS",
                    null
            );

            Mockito.when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
            Mockito.when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
            Mockito.when(viaCepService.fetchAddressByCep(dto.address().cep())).thenReturn(viaCepResponse);
            Mockito.when(userRepository.save(Mockito.any())).thenReturn(mockUser);

            UserResponseDTO result = userService.save(dto);

            Assertions.assertNotNull(result.cpf());
            Assertions.assertEquals("eren", result.name());
            Assertions.assertEquals("test@gmail.com", result.email());

        }
    }
}
