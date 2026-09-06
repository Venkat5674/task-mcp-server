package com.task.mcp_server.dto;

import com.task.mcp_server.model.Task;
import com.task.mcp_server.model.TaskPriority;
import com.task.mcp_server.model.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(Long id, String title, String description, TaskPriority priority,
                           TaskStatus status, LocalDate dueDate, Instant createdAt,
                           Instant updatedAt, Instant completedAt) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getDueDate(), task.getCreatedAt(), task.getUpdatedAt(), task.getCompletedAt());
    }
}