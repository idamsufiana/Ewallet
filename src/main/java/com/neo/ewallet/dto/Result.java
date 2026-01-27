package com.neo.ewallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Result {
    private String status;
    private Long transactionId;
    private BigDecimal newBalance;
    private String message;

    public Result(String error, Long transactionId, BigDecimal newBalance, String insufficientFunds) {
        this.status = error;
        this.transactionId = transactionId;
        this.newBalance = newBalance;
        this.message = insufficientFunds;
    }
}
