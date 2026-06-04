package com.noda.api.dtos;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record  TransactionRequestDTO(
        @NotNull(message = "Account ID cannot be missing")
        Long accountId,

        @NotNull(message = "Amount cannot be missing")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {}
