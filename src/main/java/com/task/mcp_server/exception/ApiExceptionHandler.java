package com.task.mcp_server.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(TaskNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidTaskOperationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> invalidOperation(InvalidTaskOperationException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "INVALID_TASK_OPERATION", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst().orElse("Invalid request");
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
    }

    private Map<String, Object> error(HttpStatus status, String code, String message, HttpServletRequest request) {
        return Map.of("timestamp", Instant.now(), "status", status.value(), "error", code,
                "message", message, "path", request.getRequestURI());
    }
}