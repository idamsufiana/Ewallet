package com.neo.ewallet.controller;

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

    private final EWalletService wallet;

    public TransactionController(EWalletService wallet) {
        this.wallet = wallet;
    }

    public record Req(long user_id, BigDecimal amount) {}

    @PostMapping("/credit")
    public ResponseEntity<?> credit(@RequestBody Req req) {
        try {
            var res = wallet.credit(req.user_id(), req.amount());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "transaction_id", res.transactionId(),
                    "new_balance", res.newBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody Req req) {
        try {
            var res = wallet.debit(req.user_id(), req.amount());
            if ("error".equals(res.status())) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", res.message()));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "transaction_id", res.transactionId(),
                    "new_balance", res.newBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}