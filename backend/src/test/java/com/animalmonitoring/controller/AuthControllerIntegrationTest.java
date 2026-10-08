package com.animalmonitoring.controller;

import com.animalmonitoring.dto.request.LoginRequest;
import com.animalmonitoring.dto.request.RegisterRequest;
import com.animalmonitoring.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the auth endpoints using an in-memory H2 database.
 * Each test is transactional so data is rolled back automatically.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Auth Controller integration tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ------------------------------------------------------------------ register
    @Test
    @DisplayName("POST /api/auth/register: valid request returns 201")
    void register_validRequest_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Integration User");
        req.setEmail("integration@test.com");
        req.setPassword("Password1");
        req.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("integration@test.com"))
                .andExpect(jsonPath("$.data.role").value("FARMER"));
    }

    @Test
    @DisplayName("POST /api/auth/register: invalid email returns 400")
    void register_invalidEmail_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Bad User");
        req.setEmail("not-an-email");
        req.setPassword("Password1");
        req.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register: weak password returns 400")
    void register_weakPassword_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Weak User");
        req.setEmail("weak@test.com");
        req.setPassword("short");   // violates @Size(min=8)
        req.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ login
    @Test
    @DisplayName("POST /api/auth/login: bad credentials returns 401")
    void login_badCredentials_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("nobody@test.com");
        req.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login: registered user can log in and gets a token")
    void login_registeredUser_returnsToken() throws Exception {
        // Step 1: register
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Login Test User");
        reg.setEmail("logintest@test.com");
        reg.setPassword("Password1");
        reg.setRole(Role.FARMER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        // Step 2: login
        LoginRequest login = new LoginRequest();
        login.setEmail("logintest@test.com");
        login.setPassword("Password1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }
}
