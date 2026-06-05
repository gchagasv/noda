package com.noda.api.dtos;


import com.noda.api.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        BigDecimal amount,
        String type,
        LocalDateTime timestamp,
        String sourceAccountNumber,
        String sourceAccountOwner,
        String destinationAccountNumber,
        String destinationAccountOwner
) {
    // factory constructor
    public TransactionResponseDTO(Transaction tx) {
        this(
                tx.getId(),
                tx.getAmount(),
                tx.getTransactionType().name(),
                tx.getTimestamp(),
                tx.getSourceAccount() != null ? tx.getSourceAccount().getAccountNumber() : null,
                tx.getSourceAccount() != null ? tx.getSourceAccount().getUser().getName() : null,
                tx.getDestinationAccount() != null ? tx.getDestinationAccount().getAccountNumber() : null,
                tx.getDestinationAccount() != null ? tx.getDestinationAccount().getUser().getName() : null
        );
    }
}