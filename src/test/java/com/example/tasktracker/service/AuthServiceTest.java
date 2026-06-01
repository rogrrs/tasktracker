package com.example.tasktracker.service;

import com.example.tasktracker.dto.AuthDto.RegistrationRequest;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование аутентификации (AuthService)")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private JavaMailSender mailSender;

    @InjectMocks private AuthService authService;

    @Test
    @DisplayName("Регистрация: ошибка при занятом Email")
    void register_EmailAlreadyExists_ThrowsException() {
        RegistrationRequest req = new RegistrationRequest("exists@test.com", "pass", "pass");
        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }
}