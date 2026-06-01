package com.example.tasktracker.service;

import com.example.tasktracker.dto.TaskDto.TaskRequest;
import com.example.tasktracker.dto.TaskDto.TaskResponse;
import com.example.tasktracker.model.Status;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование логики управления задачами (TaskService)")
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private JavaMailSender mailSender; // Важно замокать почту

    @InjectMocks private TaskService taskService;

    private User owner;
    private User stranger;
    private Task task;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).email("owner@test.com").build();
        stranger = User.builder().id(2L).email("stranger@test.com").build();
        task = Task.builder().id(10L).title("Test Task").owner(owner).status(Status.WAITING).build();
    }

    @Test
    @DisplayName("Создание задачи: проверка корректности заполнения полей")
    void createTask_Success() {
        when(taskRepository.save(any())).thenReturn(task);
        TaskResponse res = taskService.createTask(new TaskRequest("Title", "Desc"), owner);

        assertThat(res.status()).isEqualTo(Status.WAITING);
        assertThat(res.ownerId()).isEqualTo(owner.getId());
        verify(taskRepository).save(any());
    }

    @Test
    @DisplayName("Обновление статуса: переход в DONE должен ставить дату завершения")
    void updateStatus_ToDone_SetsDate() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        TaskResponse res = taskService.updateStatus(10L, true, owner);

        assertThat(res.status()).isEqualTo(Status.DONE);
        assertThat(res.doneAt()).isNotNull();
    }

    @Test
    @DisplayName("Безопасность: запрет на удаление чужой задачи")
    void deleteTask_ForbiddenForStranger() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(ResponseStatusException.class, () ->
                taskService.deleteTask(10L, stranger)
        );
        verify(taskRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Назначение исполнителя: проверка отправки уведомления")
    void assignTask_SendsEmail() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(stranger));
        when(taskRepository.save(any())).thenReturn(task);

        taskService.assignTask(10L, 2L, owner);

        verify(mailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }
}