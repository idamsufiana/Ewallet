package com.neo.ewallet.controller;

import com.neo.ewallet.dto.Result;
import com.neo.ewallet.dto.TransactionRequest;
import com.neo.ewallet.dto.TransactionResponse;
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
        try {
            Result res = eWalletService.credit(req.getUser_id(), req.getAmount());
            return ResponseEntity.ok(new TransactionResponse("success", res.getTransactionId(), res.getNewBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody TransactionRequest req) {
        try {
            Result res = eWalletService.debit(req.getUser_id(), req.getAmount());
            if ("error".equals(res.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", res.getMessage()));
            }
            return ResponseEntity.ok(new TransactionResponse("success", res.getTransactionId(), res.getNewBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}