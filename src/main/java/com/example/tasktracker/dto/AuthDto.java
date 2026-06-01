package com.example.tasktracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AuthDto {
    public record RegistrationRequest(
            @Email String email,
            @Size(min = 4) String password,
            String repeatPassword
    ) {}

    public record LoginRequest(
            @Email String email,
            String password
    ) {}

    public record UserResponse(Long id, String email) {}

    public record TokenResponse(String token) {}
}