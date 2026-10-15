package com.devforge.apex.controller;

import com.devforge.apex.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Student initializes course checkout
    @PostMapping("/initialize/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> initializePayment(
            @PathVariable Long courseId,
            @RequestParam BigDecimal amount,
            Authentication authentication
    ) {
        Map<String, Object> paystackResponse = paymentService.initializePayment(courseId, amount, authentication);
        return ResponseEntity.ok(paystackResponse);
    }

    // Paystack Webhook (Called automatically by Paystack when payment succeeds)
    @PostMapping("/webhook")
    public ResponseEntity<String> handlePaystackWebhook(@RequestBody Map<String, Object> payload) {
        paymentService.handleWebhookEvent(payload);
        return ResponseEntity.ok("Webhook received successfully");
    }
}