package com.example.tasktracker.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDto {
    public record RegistrationRequest(
            @Pattern(
                    regexp = "^[A-Za-z0-9._%+-]+@(gazprom-neft\\.ru|mail\\.ru|yandex\\.ru|gmail\\.com)$",
                    message = "Допустима только латиница и домены gazprom-neft.ru, mail.ru, yandex.ru, gmail.com"
            )
            String email,

            @Size(min = 4, message = "Пароль должен быть не менее 4 символов")
            String password,

            String repeatPassword
    ) {}

    public record LoginRequest(
            @Pattern(
                    regexp = "^[A-Za-z0-9._%+-]+@(gazprom-neft\\.ru|mail\\.ru|yandex\\.ru|gmail\\.com)$",
                    message = "Допустима только латиница и домены: gazprom-neft.ru, mail.ru, yandex.ru, gmail.com"
            )
            String email,

            String password
    ) {}

    public record UserResponse(Long id, String email) {}

    public record TokenResponse(String token) {}
}
