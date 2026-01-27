package com.neo.ewallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest{
    @JsonProperty("user_id")
    private long userId;
    private BigDecimal amount;
}
