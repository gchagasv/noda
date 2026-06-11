package com.noda.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyRequestDTO (
    @NotBlank @Email String email,
    @NotBlank String code
) {}
