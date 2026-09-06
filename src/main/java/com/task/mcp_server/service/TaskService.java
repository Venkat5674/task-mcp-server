package com.task.mcp_server.service;

import com.task.mcp_server.dto.CreateTaskRequest;
import com.task.mcp_server.dto.TaskResponse;
import com.task.mcp_server.dto.TaskStatistics;
import com.task.mcp_server.dto.UpdateTaskRequest;
import com.task.mcp_server.exception.InvalidTaskOperationException;
import com.task.mcp_server.exception.TaskNotFoundException;
import com.task.mcp_server.model.Task;
import com.task.mcp_server.model.TaskPriority;
import com.task.mcp_server.model.TaskStatus;
import com.task.mcp_server.repository.TaskRepository;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task(request.title(), request.description(),
                request.priority() == null ? TaskPriority.MEDIUM : request.priority(), request.dueDate());
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        return TaskResponse.from(findTask(id));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(TaskStatus status, TaskPriority priority, String search) {
        List<Task> tasks;
        if (search != null && !search.isBlank()) tasks = taskRepository.findByTitleContainingIgnoreCase(search);
        else if (status != null && priority != null) tasks = taskRepository.findByStatusAndPriority(status, priority);
        else if (status != null) tasks = taskRepository.findByStatus(status);
        else if (priority != null) tasks = taskRepository.findByPriority(priority);
        else tasks = taskRepository.findAll();
        return tasks.stream().map(TaskResponse::from).toList();
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = findTask(id);
        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.priority() != null) task.setPriority(request.priority());
        if (request.dueDate() != null) task.setDueDate(request.dueDate());
        if (request.status() != null) applyStatus(task, request.status());
        return TaskResponse.from(task);
    }

    public void deleteTask(Long id) {
        taskRepository.delete(findTask(id));
    }

    public TaskResponse completeTask(Long id) {
        Task task = findTask(id);
        if (task.getStatus() == TaskStatus.CANCELLED) {
            throw new InvalidTaskOperationException("Cancelled tasks cannot be completed");
        }
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(java.time.Instant.now());
        return TaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        return taskRepository.findByDueDateBeforeAndStatusNotAndStatusNot(LocalDate.now(),
                        TaskStatus.COMPLETED, TaskStatus.CANCELLED)
                .stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics() {
        Map<TaskStatus, Long> counts = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) counts.put(status, taskRepository.countByStatus(status));
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        long completed = counts.get(TaskStatus.COMPLETED);
        return new TaskStatistics(total, counts, getOverdueTasks().size(), total == 0 ? 0.0 : (double) completed / total);
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    private void applyStatus(Task task, TaskStatus status) {
        if (status == TaskStatus.COMPLETED) {
            completeTask(task.getId());
            return;
        }
        task.setStatus(status);
        if (status != TaskStatus.COMPLETED) task.setCompletedAt(null);
    }
}