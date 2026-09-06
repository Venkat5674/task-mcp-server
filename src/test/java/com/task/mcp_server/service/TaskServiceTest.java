package com.task.mcp_server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.task.mcp_server.dto.CreateTaskRequest;
import com.task.mcp_server.exception.InvalidTaskOperationException;
import com.task.mcp_server.model.Task;
import com.task.mcp_server.model.TaskStatus;
import com.task.mcp_server.repository.TaskRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskRepository taskRepository;
    @InjectMocks TaskService taskService;

    @Test
    void createsMediumPriorityTaskByDefault() {
        Task task = new Task("Interview", null, com.task.mcp_server.model.TaskPriority.MEDIUM, null);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        assertEquals(com.task.mcp_server.model.TaskPriority.MEDIUM,
                taskService.createTask(new CreateTaskRequest("Interview", null, null, null)).priority());
    }

    @Test
    void cancelledTaskCannotBeCompleted() {
        Task task = new Task("Cancelled", null, com.task.mcp_server.model.TaskPriority.LOW, null);
        task.setStatus(TaskStatus.CANCELLED);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(InvalidTaskOperationException.class, () -> taskService.completeTask(1L));
    }
}