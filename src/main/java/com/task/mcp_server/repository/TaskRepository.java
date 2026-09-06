package com.task.mcp_server.repository;

import com.task.mcp_server.model.Task;
import com.task.mcp_server.model.TaskPriority;
import com.task.mcp_server.model.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(TaskPriority priority);
    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);
    List<Task> findByTitleContainingIgnoreCase(String title);
    List<Task> findByDueDateBeforeAndStatusNotAndStatusNot(LocalDate date, TaskStatus first, TaskStatus second);
    long countByStatus(TaskStatus status);
}