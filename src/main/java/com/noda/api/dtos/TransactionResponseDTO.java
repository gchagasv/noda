package com.noda.api.dtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long transactionId,
        BigDecimal amount,
        String transactionType,
        LocalDateTime timestamp,
        String sourceAccountNumber,
        String sourceAccountName,
        String destinationAccountNumber,
        String destinationAccountOwnerName
) {}