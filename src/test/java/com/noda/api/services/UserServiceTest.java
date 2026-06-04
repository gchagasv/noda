package com.noda.api.services;


import com.noda.api.dtos.*;
import com.noda.api.exceptions.CepNotFoundException;
import com.noda.api.exceptions.CpfAlreadyRegisteredException;
import com.noda.api.exceptions.EmailAlreadyRegisteredException;
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

        @Test
        @DisplayName("Should throw CpfAlreadyRegisteredException when user try to pass an already used CPF")
        void shouldThrowExceptionWhenCpfIsDuplicated() {

            String mockUserCep = "99888777";
            String mockUserNumber = "26";
            AddressRequestDTO addressDTO = new AddressRequestDTO(mockUserCep, mockUserNumber, null);

            UserRequestDTO dto = new UserRequestDTO("eren", "04581157032", "test@gmail.com",
                    "senha123", LocalDate.of(2006, 6, 3), addressDTO);


            User existingUser = new User();
            existingUser.setCpf("04581157032");

            Mockito.when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(existingUser));

            Assertions.assertThrows(CpfAlreadyRegisteredException.class, () -> {
                userService.save(dto);
            });

            Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Should throw EmailAlreadyRegisteredException when user try to pass an already used email")
        void shouldThrowExceptionWhenEmailIsDuplicated() {

            String mockUserCep = "99888777";
            String mockUserNumber = "26";
            AddressRequestDTO addressDTO = new AddressRequestDTO(mockUserCep, mockUserNumber, null);

            UserRequestDTO dto = new UserRequestDTO("eren", "04581157032", "test@gmail.com",
                    "senha123", LocalDate.of(2006, 6, 3), addressDTO);

            User existingUser = new User();
            existingUser.setEmail("test@gmail.com");

            Mockito.when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
            Mockito.when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(existingUser));

            Assertions.assertThrows(EmailAlreadyRegisteredException.class, () -> {
                userService.save(dto);
            });

            Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Should throw CepNotFoundException when cep is not found")
        void shouldThrowExceptionWhenCepIsNotFound() {
            String mockUserCep = "99888777";
            String mockUserNumber = "26";
            AddressRequestDTO addressDTO = new AddressRequestDTO(mockUserCep, mockUserNumber, null);

            UserRequestDTO dto = new UserRequestDTO("eren", "04581157032", "test@gmail.com",
                    "senha123", LocalDate.of(2006, 6, 3), addressDTO);


            Mockito.when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
            Mockito.when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
            Mockito.when(viaCepService.fetchAddressByCep(dto.address().cep()))
                    .thenThrow(new CepNotFoundException("CEP not found: " + mockUserCep));

            Assertions.assertThrows(CepNotFoundException.class, () -> {
                userService.save(dto);
            });
        }
    }
}
