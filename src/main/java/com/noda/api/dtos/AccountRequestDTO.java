package com.noda.api.dtos;


import com.noda.api.models.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDTO {
    private AccountType accountType;
    private Long userId;
}
