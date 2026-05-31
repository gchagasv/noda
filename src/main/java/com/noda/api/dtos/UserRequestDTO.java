package com.noda.api.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "CPF is required") String cpf,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
        @NotBlank(message = "Password is required") String password,
        @NotNull(message = "Birthday is required")LocalDate birthday
) {}
