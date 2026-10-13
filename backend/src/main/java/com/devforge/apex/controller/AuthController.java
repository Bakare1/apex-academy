package com.devforge.apex.controller;

import com.devforge.apex.auth.*;
import com.devforge.apex.repo.UserRepository;
import com.devforge.apex.user.User;
import com.devforge.apex.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Find the user entity from the database using the email (username)
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map to UserResponse DTO
        UserResponse response = UserResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}