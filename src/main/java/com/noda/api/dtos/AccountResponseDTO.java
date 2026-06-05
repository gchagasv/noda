package com.noda.api.dtos;

import com.noda.api.models.Account;
import com.noda.api.models.enums.AccountType;
import java.math.BigDecimal;

public record AccountResponseDTO (
     Long accountId,
     String accountNumber,
     AccountType accountType,
     BigDecimal balance,
     String ownerName
     ) {

     public AccountResponseDTO(Account account) {
          this(
                  account.getId(),
                  account.getAccountNumber(),
                  account.getAccountType(),
                  account.getBalance(),
                  account.getUser().getName()
          );
     }
}

