package com.example.tasktracker.controller;

import com.example.tasktracker.dto.AuthDto.*;
import com.example.tasktracker.model.User;
import com.example.tasktracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegistrationRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(new TokenResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(new TokenResponse(token));
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return new UserResponse(user.getId(), user.getEmail());
    }

    @GetMapping("/users")
    public java.util.List<UserResponse> getAllUsers() {
        return authService.getAllUsers();
    }
}