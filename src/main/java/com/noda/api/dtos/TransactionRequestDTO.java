package com.noda.api.dtos;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequestDTO {
    private Long id;
    private BigDecimal amount;
}
