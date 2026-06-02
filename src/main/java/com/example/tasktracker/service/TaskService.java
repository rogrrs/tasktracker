package com.example.tasktracker.service;

import com.example.tasktracker.dto.TaskDto.*;
import com.example.tasktracker.model.Status;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public TaskResponse createTask(TaskRequest request, User owner) {
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(Status.WAITING)
                .createdAt(LocalDateTime.now())
                .owner(owner)
                .build();
        return mapToResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getAllMyTasks(User owner) {
        return taskRepository.findAllByOwner(owner).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskResponse getTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkAccess(task, user);
        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkAccess(task, user);
        task.setTitle(request.title());
        task.setDescription(request.description());
        return mapToResponse(taskRepository.save(task));
    }

    public TaskResponse updateStatus(Long id, boolean done, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkAccess(task, user);

        task.setStatus(done ? Status.DONE : Status.WAITING);

        LocalDateTime doneDate = switch (task.getStatus()) {
            case DONE -> LocalDateTime.now();
            case WAITING -> null;
        };

        task.setDoneAt(doneDate);
        return mapToResponse(taskRepository.save(task));
    }

    public TaskResponse assignTask(Long id, Long assigneeId, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkAccess(task, user);

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));

        task.setAssignee(assignee);
        Task savedTask = taskRepository.save(task);

        sendAssignmentEmail(assignee.getEmail(), task.getTitle());

        return mapToResponse(savedTask);
    }

    private void sendAssignmentEmail(String email, String taskTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("New Task Assigned");
            message.setText("Hello! You have been assigned as the executor for the task: " + taskTitle);
            mailSender.send(message);
            log.info("Assignment email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send assignment email: {}", e.getMessage());
        }
    }

    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkAccess(task, user);
        taskRepository.delete(task);
    }

    private void checkAccess(Task task, User user) {
        if (!task.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your task");
        }
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getDoneAt(),
                task.getOwner().getId(),
                task.getAssignee() != null ? task.getAssignee().getId() : null
        );
    }

    public List<TaskResponse> getAllTasksInSystem() {
        return taskRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }
}
