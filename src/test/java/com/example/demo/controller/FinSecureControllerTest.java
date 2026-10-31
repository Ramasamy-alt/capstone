package com.example.demo.controller;

import com.example.demo.model.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class FinSecureControllerTest {

    private final FinSecureController controller = new FinSecureController();

    @Test
    public void testHealthCheckEndpoint() {
        ResponseEntity<Object> response = controller.healthCheck();
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testSuccessfulValidation() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId("TXN-001");
        request.setAmount(150.00);
        request.setCurrency("USD");
        request.setAccountNumber("ACC-VALID-123");

        ResponseEntity<Object> response = controller.validateTransaction(request);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testFailedValidationBlacklisted() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId("TXN-002");
        request.setAmount(150.00);
        request.setCurrency("USD");
        request.setAccountNumber("ACC-BLACKLISTED-999");

        ResponseEntity<Object> response = controller.validateTransaction(request);
        assertEquals(200, response.getStatusCode().value());
    }
}