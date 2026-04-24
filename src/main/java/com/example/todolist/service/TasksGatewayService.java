package com.example.todolist.service;

import com.example.todolist.client.ExternalTasksClient;
import com.example.todolist.dto.ExternalTaskCreateDto;
import com.example.todolist.dto.ExternalTaskDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public ResponseEntity<ExternalTaskDto> createTask(ExternalTaskCreateDto dto) {
        return externalTasksClient.createTask(dto);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public ExternalTaskDto getTask(Long id) {
        return externalTasksClient.getTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "listTasksFallback")
    public List<ExternalTaskDto> listTasks(Boolean completed, Integer limit) {
        return externalTasksClient.listTasks(completed, limit);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public ResponseEntity<Void> deleteTask(Long id) {
        return externalTasksClient.deleteTask(id);
    }

    public ResponseEntity<ExternalTaskDto> createTaskFallback(ExternalTaskCreateDto dto, Throwable t) {
        rethrowIfDomainException(t);
        log.warn("createTask circuit-breaker fallback: {}", t.getMessage());
        return ResponseEntity.status(503)
                .body(new ExternalTaskDto(null, "Service temporarily unavailable", null, false));
    }

    public ExternalTaskDto getTaskFallback(Long id, Throwable t) {
        rethrowIfDomainException(t);
        log.warn("getTask circuit-breaker fallback for id={}: {}", id, t.getMessage());
        return new ExternalTaskDto(id, "Service temporarily unavailable", null, false);
    }

    public List<ExternalTaskDto> listTasksFallback(Boolean completed, Integer limit, Throwable t) {
        rethrowIfDomainException(t);
        log.warn("listTasks circuit-breaker fallback: {}", t.getMessage());
        return List.of();
    }

    public ResponseEntity<Void> deleteTaskFallback(Long id, Throwable t) {
        rethrowIfDomainException(t);
        log.warn("deleteTask circuit-breaker fallback for id={}: {}", id, t.getMessage());
        return ResponseEntity.status(503).build();
    }

    private void rethrowIfDomainException(Throwable t) {
        if (t instanceof com.example.todolist.exception.TaskNotFoundException e) throw e;
        if (t instanceof io.github.resilience4j.ratelimiter.RequestNotPermitted e) throw e;
    }
}
