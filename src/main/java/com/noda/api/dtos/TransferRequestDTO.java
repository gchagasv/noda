package com.noda.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequestDTO(
        @NotNull(message = "Source ID cannot be null")
        Long sourceId,

        @NotNull(message = "Target ID cannot be null")
        Long targetId,

        @NotNull(message = "Amount cannot be missing")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {}
