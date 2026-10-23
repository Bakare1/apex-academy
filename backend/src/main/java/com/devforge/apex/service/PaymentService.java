package com.devforge.apex.service;

import com.devforge.apex.model.Course;
import com.devforge.apex.model.Enrollment;
import com.devforge.apex.model.Payment;
import com.devforge.apex.repo.CourseRepository;
import com.devforge.apex.repo.PaymentRepository;
import com.devforge.apex.repo.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository; // Injected to fetch dynamic course price

    @Value("${paystack.secret-key}")
    private String paystackSecretKey;

    @Value("${paystack.base-url}")
    private String paystackBaseUrl;

    public Map<String, Object> initializePayment(Long courseId, Authentication authentication) {
        String studentEmail = authentication.getName();
        String reference = "DEVFORGE-" + UUID.randomUUID().toString();

        // Fetch course dynamically from database to get official price
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        BigDecimal amount = BigDecimal.valueOf(course.getPrice());

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

        // Convert amount to lowest currency unit (Kobo: multiply by 100)
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

    @Transactional
    public void handleWebhookEvent(Map<String, Object> payload) {
        // Handle Paystack webhook confirmation
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        if (data != null && "charge.success".equals(payload.get("event"))) {
            String reference = (String) data.get("reference");

            Payment payment = paymentRepository.findByReference(reference)
                    .orElseThrow(() -> new RuntimeException("Payment reference not found: " + reference));

            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            // Automatically enroll student in the course upon successful payment
            boolean alreadyEnrolled = enrollmentRepository.existsByStudentEmailAndCourseId(
                    payment.getStudentEmail(),
                    payment.getCourseId()
            );

            if (!alreadyEnrolled) {
                Enrollment enrollment = Enrollment.builder()
                        .studentEmail(payment.getStudentEmail())
                        .courseId(payment.getCourseId())
                        .build();

                enrollmentRepository.save(enrollment);
                System.out.println("Student " + payment.getStudentEmail() + " successfully enrolled in course " + payment.getCourseId());
            }
        }
    }
}