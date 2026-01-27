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

    public TransactionResponse(String success, String message) {
        this.status = success;
        this.message = message;
    }

    public static TransactionResponse success(Long txId, BigDecimal balance) {
        return new TransactionResponse("success", txId, balance);
    }

    public static TransactionResponse error(String message) {
        return new TransactionResponse("error", message);
    }
}
