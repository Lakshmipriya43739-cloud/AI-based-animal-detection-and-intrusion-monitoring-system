package com.animalmonitoring.service;

import com.animalmonitoring.dto.request.LoginRequest;
import com.animalmonitoring.dto.request.RegisterRequest;
import com.animalmonitoring.dto.response.AuthResponse;
import com.animalmonitoring.dto.response.UserResponse;
import com.animalmonitoring.entity.Role;
import com.animalmonitoring.entity.User;
import com.animalmonitoring.exception.DuplicateResourceException;
import com.animalmonitoring.repository.UserRepository;
import com.animalmonitoring.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService unit tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository, passwordEncoder, authenticationManager, jwtTokenProvider);
    }

    // ------------------------------------------------------------------ register
    @Test
    @DisplayName("register: success - new email returns UserResponse")
    void register_newEmail_returnsUserResponse() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Alice");
        req.setEmail("alice@example.com");
        req.setPassword("Password1");
        req.setRole(Role.FARMER);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("$hash");

        User saved = User.builder()
                .id(1L).name("Alice").email("alice@example.com")
                .password("$hash").role(Role.FARMER).enabled(true).build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = authService.register(req);

        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getRole()).isEqualTo(Role.FARMER);
        verify(passwordEncoder).encode("Password1");
    }

    @Test
    @DisplayName("register: duplicate email throws DuplicateResourceException")
    void register_duplicateEmail_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("dup@example.com");
        req.setPassword("Password1");
        req.setName("Dup");
        req.setRole(Role.FARMER);

        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("dup@example.com");
    }

    @Test
    @DisplayName("register: password is BCrypt-hashed, never stored in plain text")
    void register_passwordIsEncoded() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Bob");
        req.setEmail("bob@example.com");
        req.setPassword("PlainText1");
        req.setRole(Role.OFFICER);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("PlainText1")).thenReturn("$bcrypt$hash");

        User saved = User.builder().id(2L).name("Bob").email("bob@example.com")
                .password("$bcrypt$hash").role(Role.OFFICER).enabled(true).build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        authService.register(req);

        verify(passwordEncoder, times(1)).encode("PlainText1");
    }

    // ------------------------------------------------------------------ login
    @Test
    @DisplayName("login: valid credentials return JWT token")
    void login_validCredentials_returnsToken() {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@example.com");
        req.setPassword("Password1");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("jwt.token.value");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        User user = User.builder().id(1L).name("Alice").email("alice@example.com")
                .role(Role.FARMER).enabled(true).build();
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(req);

        assertThat(response.getToken()).isEqualTo("jwt.token.value");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("login: bad credentials propagate BadCredentialsException")
    void login_badCredentials_throws() {
        LoginRequest req = new LoginRequest();
        req.setEmail("wrong@example.com");
        req.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }
}
