package com.noda.api.dtos;

import jakarta.validation.constraints.NotNull;
import com.noda.api.models.enums.AccountType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccountRequestDTO {

    @NotNull(message = "Account type must be chosen.")
    private AccountType accountType;

    @NotNull(message = "User id cannot be null.")
    private Long userId;
}
