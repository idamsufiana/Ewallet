package com.neo.ewallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionResponse {
    private String status;
    private Long transactionId;
    private BigDecimal newBalance;
    private String message;

    public TransactionResponse(String success, Long transactionId, BigDecimal newBalance) {
        this.status = success;
        this.transactionId = transactionId;
        this.newBalance = newBalance;
    }

    private TransactionResponse(String status, Long txId, BigDecimal balance, String message) {
        this.status = status;
        this.transactionId = txId;
        this.newBalance = balance;
        this.message = message;
    }

    public static TransactionResponse success(Long txId, BigDecimal balance) {
        return new TransactionResponse("success", txId, balance, null);
    }

    public static TransactionResponse error(String message) {
        return new TransactionResponse("error", null, null, message);
    }
}
