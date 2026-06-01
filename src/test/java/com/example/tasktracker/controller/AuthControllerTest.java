package com.example.tasktracker.controller;

import com.example.tasktracker.dto.AuthDto.RegistrationRequest;
import com.example.tasktracker.service.AuthService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционное тестирование API")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AuthService authService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("Точка входа /register: Успешный ответ")
    void register_ShouldReturn200() throws Exception {
        RegistrationRequest req = new RegistrationRequest("new@test.com", "pass123", "pass123");
        when(authService.register(any())).thenReturn("token123");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Точка входа /tasks: Должна возвращать 403 без токена")
    void tasks_WithoutToken_Returns403() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isForbidden());
    }
}