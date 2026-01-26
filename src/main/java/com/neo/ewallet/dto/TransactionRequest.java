package com.neo.ewallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest{
    private long user_id;
    private BigDecimal amount;
}
