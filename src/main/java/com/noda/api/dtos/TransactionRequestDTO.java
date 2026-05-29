package com.noda.api.dtos;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequestDTO {
   @NotNull
    private Long accountId;

    @NotNull(message = "Amount cannot be missing")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;
}
