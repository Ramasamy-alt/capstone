package com.example.demo.controller;

import com.example.demo.model.TransactionRequest;
import com.example.demo.model.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FinSecureController {

    // Health Check Endpoint (Task 1, Req 2)
    @GetMapping("/health")
    public ResponseEntity<Object> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "FinSecure Pay Validation API",
            "timestamp", java.time.Instant.now().toString()
        ));
    }

    // Transaction Validation Endpoint with Meaningful Rules (Task 1, Req 3)
    @PostMapping("/validate")
    public ResponseEntity<Object> validateTransaction(@Valid @RequestBody TransactionRequest request) {
        
        // Rule 1: Check for blacklisted or fraudulent account numbers
        if ("ACC-BLACKLISTED-999".equals(request.getAccountNumber())) {
            return ResponseEntity.ok(new TransactionResponse(
                request.getTransactionId(),
                "FAILED",
                "Transaction rejected: Account is flagged for fraudulent activity."
            ));
        }

        // Rule 2: Check for excessive single-transaction limit ($50,000 threshold)
        if (request.getAmount() != null && request.getAmount() > 50000.00) {
            return ResponseEntity.ok(new TransactionResponse(
                request.getTransactionId(),
                "FAILED",
                "Transaction rejected: Amount exceeds the maximum single-transaction limit of $50,000."
            ));
        }

        // Rule 3: Check for unsupported or restricted currencies
        if ("XXX".equalsIgnoreCase(request.getCurrency())) {
            return ResponseEntity.ok(new TransactionResponse(
                request.getTransactionId(),
                "FAILED",
                "Transaction rejected: Unsupported or restricted currency code."
            ));
        }

        // Default Case: Successful Validation
        return ResponseEntity.ok(new TransactionResponse(
            request.getTransactionId(),
            "SUCCESS",
            "Transaction passed all validation checks successfully."
        ));
    }
}