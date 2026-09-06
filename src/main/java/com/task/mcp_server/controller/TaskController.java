package com.task.mcp_server.controller;

import com.task.mcp_server.dto.CreateTaskRequest;
import com.task.mcp_server.dto.TaskResponse;
import com.task.mcp_server.dto.TaskStatistics;
import com.task.mcp_server.dto.UpdateTaskRequest;
import com.task.mcp_server.model.TaskPriority;
import com.task.mcp_server.model.TaskStatus;
import com.task.mcp_server.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @GetMapping
    public List<TaskResponse> getTasks(@RequestParam(required = false) TaskStatus status,
                                       @RequestParam(required = false) TaskPriority priority,
                                       @RequestParam(required = false) String search) {
        return taskService.getAllTasks(status, priority, search);
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id) { return taskService.getTaskById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) { return taskService.createTask(request); }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) { taskService.deleteTask(id); }

    @PatchMapping("/{id}/complete")
    public TaskResponse completeTask(@PathVariable Long id) { return taskService.completeTask(id); }

    @GetMapping("/statistics")
    public TaskStatistics statistics() { return taskService.getTaskStatistics(); }

    @GetMapping("/overdue")
    public List<TaskResponse> overdue() { return taskService.getOverdueTasks(); }
}