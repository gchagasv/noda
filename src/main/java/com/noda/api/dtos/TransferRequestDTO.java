package com.noda.api.dtos;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequestDTO {
    private Long sourceId;
    private Long targetId;
    private BigDecimal amount;
}
