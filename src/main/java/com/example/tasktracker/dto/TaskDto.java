package com.example.tasktracker.dto;

import com.example.tasktracker.model.Status;
import java.time.LocalDateTime;

public class TaskDto {
    public record TaskRequest(String title, String description) {}

    public record TaskResponse(
            Long id,
            String title,
            String description,
            Status status,
            LocalDateTime createdAt,
            LocalDateTime doneAt,
            Long ownerId,
            Long assigneeId
    ) {}
}