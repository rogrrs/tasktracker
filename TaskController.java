package com.example.tasktracker.controller;

import com.example.tasktracker.dto.TaskDto.*;
import com.example.tasktracker.model.User;
import com.example.tasktracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public TaskResponse create(@RequestBody TaskRequest request, @AuthenticationPrincipal User user) {
        return taskService.createTask(request, user);
    }

    @GetMapping
    public List<TaskResponse> getAll(@AuthenticationPrincipal User user) {
        return taskService.getAllMyTasks(user);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return taskService.getTaskById(id, user);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @RequestBody TaskRequest request, @AuthenticationPrincipal User user) {
        return taskService.updateTask(id, request, user);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Boolean> body, @AuthenticationPrincipal User user) {
        return taskService.updateStatus(id, body.get("done"), user);
    }

    @PatchMapping("/{id}/assignee")
    public TaskResponse assign(@PathVariable Long id, @RequestBody java.util.Map<String, Long> body, @AuthenticationPrincipal User user) {
        return taskService.assignTask(id, body.get("assigneeId"), user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
    }

    @GetMapping("/all-system")
    public List<TaskResponse> getAllSystemTasks() {
        return taskService.getAllTasksInSystem();
    }
}