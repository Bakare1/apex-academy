package com.devforge.apex;

import com.devforge.apex.auth.LoginRequest;
import com.devforge.apex.auth.RegisterRequest;
import com.devforge.apex.repo.UserRepository;
import com.devforge.apex.user.Role;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Alice Developer")
                .email("alice@devforge.com")
                .password("securePassword123")
                .role(Role.ROLE_STUDENT)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.email").value("alice@devforge.com"))
                .andExpect(jsonPath("$.role").value("ROLE_STUDENT"));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        // First register the user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullName("Bob Builder")
                .email("bob@devforge.com")
                .password("password123")
                .role(Role.ROLE_INSTRUCTOR)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Now test login
        LoginRequest loginRequest = LoginRequest.builder()
                .email("bob@devforge.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.email").value("bob@devforge.com"))
                .andExpect(jsonPath("$.role").value("ROLE_INSTRUCTOR"));
    }

    @Test
    void shouldReturnConflictWhenRegisteringDuplicateEmail() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Charlie Doe")
                .email("charlie@devforge.com")
                .password("password123")
                .role(Role.ROLE_STUDENT)
                .build();

        // Register first time
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Register second time with same email
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already registered: charlie@devforge.com"));
    }
}
