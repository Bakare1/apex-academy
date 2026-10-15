package com.devforge.apex.service;

import com.devforge.apex.model.Payment;
import com.devforge.apex.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${paystack.secret-key}")
    private String paystackSecretKey;

    @Value("${paystack.base-url}")
    private String paystackBaseUrl;

    public Map<String, Object> initializePayment(Long courseId, BigDecimal amount, Authentication authentication) {
        String studentEmail = authentication.getName();
        String reference = "DEVFORGE-" + UUID.randomUUID().toString();

        // Save payment record as PENDING
        Payment payment = Payment.builder()
                .reference(reference)
                .studentEmail(studentEmail)
                .courseId(courseId)
                .amount(amount)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        // Prepare request to Paystack API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackSecretKey);

        // Convert amount to lowest currency unit (e.g., Kobo: multiply by 100)
        long amountInKobo = amount.multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, Object> body = new HashMap<>();
        body.put("email", studentEmail);
        body.put("amount", amountInKobo);
        body.put("reference", reference);
        body.put("callback_url", "https://yourfrontend.com/payment-complete");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    paystackBaseUrl + "/transaction/initialize",
                    entity,
                    Map.class
            );
            return response.getBody(); // Contains authorization_url and access_code
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Paystack payment: " + e.getMessage());
        }
    }

    public void handleWebhookEvent(Map<String, Object> payload) {
        // Handle Paystack webhook confirmation
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        if (data != null && "charge.success".equals(payload.get("event"))) {
            String reference = (String) data.get("reference");

            Payment payment = paymentRepository.findByReference(reference)
                    .orElseThrow(() -> new RuntimeException("Payment reference not found: " + reference));

            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            // TODO: Automatically enroll student in the course here!
        }
    }
}