package com.task.mcp_server.dto;

import com.task.mcp_server.model.TaskPriority;
import com.task.mcp_server.model.TaskStatus;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateTaskRequest(
        @Size(max = 200) String title,
        @Size(max = 2000) String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDate dueDate) {
}