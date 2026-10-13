package com.devforge.apex.auth;

import com.devforge.apex.repo.UserRepository;
import com.devforge.apex.security.JwtService;
import com.devforge.apex.user.Role;
import com.devforge.apex.user.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT)
                .enabled(true)
                .build();

        var savedUser = userRepository.save(user);

        var springUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username(savedUser.getEmail())
                .password(savedUser.getPassword())
                .authorities(savedUser.getRole().name())
                .build();

        var jwtToken = jwtService.generateToken(springUserDetails);
        var refreshToken = jwtService.generateRefreshToken(springUserDetails);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        var springUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        var jwtToken = jwtService.generateToken(springUserDetails);
        var refreshToken = jwtService.generateRefreshToken(springUserDetails);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String userEmail = jwtService.extractUsername(request.refreshToken());

        if (userEmail != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(request.refreshToken(), userDetails)) {
                String accessToken = jwtService.generateToken(userDetails);

                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(request.refreshToken())
                        .email(userDetails.getUsername())
                        .build();
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }
}