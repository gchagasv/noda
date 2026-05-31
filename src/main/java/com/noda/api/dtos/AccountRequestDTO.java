package com.noda.api.dtos;

import jakarta.validation.constraints.NotNull;
import com.noda.api.models.enums.AccountType;

public record AccountRequestDTO(

        @NotNull(message = "Account type must be chosen.")
        AccountType accountType,

        @NotNull(message = "User id cannot be null.")
        Long userId
) {
}
