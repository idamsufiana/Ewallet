package com.neo.ewallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Result {
    private String status;
    private Long transactionId;
    private BigDecimal newBalance;
    private String message;

    public Result(String error, Long o, BigDecimal o1, String insufficientFunds) {
        this.status = error;
        this.transactionId = o;
        this.newBalance =o1;
        this.message = insufficientFunds;
    }
}
