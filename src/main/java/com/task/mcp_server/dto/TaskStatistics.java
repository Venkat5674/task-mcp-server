package com.task.mcp_server.dto;

import com.task.mcp_server.model.TaskStatus;
import java.util.Map;

public record TaskStatistics(long total, Map<TaskStatus, Long> byStatus, long overdue, double completionRate) {
}