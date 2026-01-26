package com.neo.ewallet.controller;

import com.neo.ewallet.dto.Result;
import com.neo.ewallet.dto.TransactionRequest;
import com.neo.ewallet.dto.TransactionResponse;
import com.neo.ewallet.exception.BusinessException;
import com.neo.ewallet.service.EWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final EWalletService eWalletService;

    public TransactionController(EWalletService wallet) {
        this.eWalletService = wallet;
    }


    @PostMapping("/credit")
    public ResponseEntity<?> credit(@RequestBody TransactionRequest req) {
        Result res = eWalletService.credit(req.getUser_id(), req.getAmount());
        return ResponseEntity.ok(
                TransactionResponse.success(
                        res.getTransactionId(),
                        res.getNewBalance()
                )
        );
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody TransactionRequest req) {
        Result res = eWalletService.debit(req.getUser_id(), req.getAmount());

        return ResponseEntity.ok(
                TransactionResponse.success(
                        res.getTransactionId(),
                        res.getNewBalance()
                )
        );
    }

}