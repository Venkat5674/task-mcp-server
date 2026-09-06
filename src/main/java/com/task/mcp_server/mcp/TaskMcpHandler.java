package com.task.mcp_server.mcp;

import com.task.mcp_server.dto.CreateTaskRequest;
import com.task.mcp_server.dto.TaskResponse;
import com.task.mcp_server.dto.TaskStatistics;
import com.task.mcp_server.dto.UpdateTaskRequest;
import com.task.mcp_server.model.TaskPriority;
import com.task.mcp_server.model.TaskStatus;
import com.task.mcp_server.service.TaskService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class TaskMcpHandler {
    private final TaskService taskService;

    public TaskMcpHandler(TaskService taskService) { this.taskService = taskService; }

    @McpTool(name = "createTask", description = "Create a task with an optional due date and priority.")
    public TaskResponse createTask(@McpToolParam(description = "Task title") String title,
                                   @McpToolParam(description = "Task description", required = false) String description,
                                   @McpToolParam(description = "LOW, MEDIUM, HIGH, or CRITICAL", required = false) TaskPriority priority,
                                   @McpToolParam(description = "ISO date, for example 2026-09-07", required = false) String dueDate) {
        return taskService.createTask(new CreateTaskRequest(title, description, priority, parseDate(dueDate)));
    }

    @McpTool(name = "getTask", description = "Get a task by ID.")
    public TaskResponse getTask(@McpToolParam(description = "Task ID") Long id) { return taskService.getTaskById(id); }

    @McpTool(name = "getTasks", description = "List tasks, optionally filtered by status, priority, or title search.")
    public List<TaskResponse> getTasks(@McpToolParam(description = "Task status", required = false) TaskStatus status,
                                       @McpToolParam(description = "Task priority", required = false) TaskPriority priority,
                                       @McpToolParam(description = "Title search", required = false) String search) {
        return taskService.getAllTasks(status, priority, search);
    }

    @McpTool(name = "updateTask", description = "Update editable fields on a task.")
    public TaskResponse updateTask(@McpToolParam(description = "Task ID") Long id,
                                   @McpToolParam(description = "New title", required = false) String title,
                                   @McpToolParam(description = "New description", required = false) String description,
                                   @McpToolParam(description = "New priority", required = false) TaskPriority priority,
                                   @McpToolParam(description = "New status", required = false) TaskStatus status,
                                   @McpToolParam(description = "New ISO due date", required = false) String dueDate) {
        return taskService.updateTask(id, new UpdateTaskRequest(title, description, priority, status, parseDate(dueDate)));
    }

    @McpTool(name = "deleteTask", description = "Delete a task and return confirmation.")
    public String deleteTask(@McpToolParam(description = "Task ID") Long id) {
        taskService.deleteTask(id);
        return "Task " + id + " deleted";
    }

    @McpTool(name = "completeTask", description = "Mark a task as completed.")
    public TaskResponse completeTask(@McpToolParam(description = "Task ID") Long id) { return taskService.completeTask(id); }

    @McpTool(name = "getOverdueTasks", description = "List tasks past their due date that are not completed or cancelled.")
    public List<TaskResponse> getOverdueTasks() { return taskService.getOverdueTasks(); }

    @McpTool(name = "getTaskStatistics", description = "Get totals, status counts, overdue count, and completion rate.")
    public TaskStatistics getTaskStatistics() { return taskService.getTaskStatistics(); }

    @McpResource(name = "allTasks", uri = "tasks://all", description = "All tasks")
    public List<TaskResponse> allTasks() { return taskService.getAllTasks(null, null, null); }

    @McpResource(name = "pendingTasks", uri = "tasks://pending", description = "Pending tasks")
    public List<TaskResponse> pendingTasks() { return taskService.getAllTasks(TaskStatus.PENDING, null, null); }

    @McpResource(name = "completedTasks", uri = "tasks://completed", description = "Completed tasks")
    public List<TaskResponse> completedTasks() { return taskService.getAllTasks(TaskStatus.COMPLETED, null, null); }

    @McpResource(name = "overdueTasks", uri = "tasks://overdue", description = "Overdue tasks")
    public List<TaskResponse> overdueTasks() { return taskService.getOverdueTasks(); }

    @McpPrompt(name = "daily-planning", description = "Plan today using pending and overdue task context.")
    public String dailyPlanning() { return "Review the pending and overdue tasks, then propose a focused plan for today."; }

    @McpPrompt(name = "weekly-review", description = "Review completed, pending, and overdue work.")
    public String weeklyReview() { return "Summarize completed work and identify pending or overdue tasks for the week."; }

    @McpPrompt(name = "task-prioritization", description = "Prioritize work using priority, due date, and status.")
    public String taskPrioritization() { return "Prioritize the current tasks using urgency, due date, priority, and status."; }

    private LocalDate parseDate(String value) { return value == null || value.isBlank() ? null : LocalDate.parse(value); }
}