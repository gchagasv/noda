package com.noda.api.dtos;

import jakarta.validation.constraints.NotBlank;

public record AddressRequestDTO (
        @NotBlank(message = "CEP must be chosen")
        String CEP,
        @NotBlank(message = "House or building number is required")
        String number,

        String complement
) {}
