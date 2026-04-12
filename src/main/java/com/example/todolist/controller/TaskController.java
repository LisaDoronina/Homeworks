package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskPriorityCountDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.exception.BulkOperationException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.TaskStatisticsJdbcService;
import com.example.todolist.service.TaskStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "API для управления задачами")
public class TaskController {

  private final TaskService taskService;
  private final TaskMapper taskMapper;
  private final TaskRepository taskRepository;
  private final TaskStatisticsJdbcService taskStatisticsService;

  @GetMapping
  @Operation(summary = "Получить все задачи")
  @Transactional(readOnly = true)
  public List<Task> getAllTasks() {
    List<Task> tasks = taskRepository.findAll();
    tasks.forEach(task -> task.getAttachments().size());
    return tasks;
  }

  @GetMapping("/with-attachments")
  @Operation(summary = "Получить все задачи с вложениями (решение проблемы N+1)")
  public ResponseEntity<List<TaskResponseDto>> getAllTasksWithAttachments() {
    List<Task> tasks = taskService.getAllTasksWithAttachments();
    List<TaskResponseDto> response = tasks.stream()
            .map(taskMapper::toResponseDto)
            .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить задачу по ID")
  public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
    Task task = taskService.getTaskById(id)
            .orElseThrow(() -> new RuntimeException("Task not found: " + id));
    return ResponseEntity.ok(taskMapper.toResponseDto(task));
  }

  @GetMapping("/{id}/with-attachments")
  @Operation(summary = "Получить задачу с вложениями по ID")
  public ResponseEntity<TaskResponseDto> getTaskByIdWithAttachments(@PathVariable Long id) {
    Task task = taskService.getTaskByIdWithAttachments(id)
            .orElseThrow(() -> new RuntimeException("Task not found: " + id));
    return ResponseEntity.ok(taskMapper.toResponseDto(task));
  }

  @PostMapping
  @Operation(summary = "Создать новую задачу")
  public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskCreateDto createDto) {
    Task task = taskMapper.toEntity(createDto);
    Task createdTask = taskService.createTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toResponseDto(createdTask));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить задачу")
  public ResponseEntity<TaskResponseDto> updateTask(
          @PathVariable Long id,
          @Valid @RequestBody TaskUpdateDto updateDto) {
    Task task = taskMapper.updateEntity(updateDto, new Task());
    Task updatedTask = taskService.updateTask(id, task);
    return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить задачу")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/bulk/complete")
  @Operation(summary = "Массовое обновление статуса задач (демонстрация транзакций)")
  public ResponseEntity<Map<String, Object>> bulkCompleteTasks(@RequestBody List<Long> ids) {
    int updatedCount = taskService.bulkCompleteTasks(ids);
    return ResponseEntity.ok(Map.of(
            "message", "Задачи успешно обновлены",
            "updatedCount", updatedCount,
            "status", "success"
    ));
  }

  @PostMapping("/bulk/delete")
  @Operation(summary = "Массовое удаление задач (демонстрация транзакций)")
  public ResponseEntity<Map<String, Object>> bulkDeleteTasks(@RequestBody List<Long> ids) {
    int deletedCount = taskService.bulkDeleteTasks(ids);
    return ResponseEntity.ok(Map.of(
            "message", "Задачи успешно удалены",
            "deletedCount", deletedCount,
            "status", "success"
    ));
  }

  @ExceptionHandler(com.example.todolist.exception.BulkOperationException.class)
  public ResponseEntity<Map<String, Object>> handleBulkOperationException(BulkOperationException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "error", e.getMessage(),
            "missingIds", e.getMissingIds(),
            "status", "failed"
    ));
  }

  @GetMapping("/statistics/by-priority")
  @Operation(summary = "Статистика по приоритетам (JDBC)")
  public ResponseEntity<List<TaskPriorityCountDto>> getStatisticsByPriority() {
    return ResponseEntity.ok(taskStatisticsService.getTasksCountByPriority());
  }

  @GetMapping("/statistics/general")
  @Operation(summary = "Общая статистика")
  public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
    return ResponseEntity.ok(taskStatisticsService.getGeneralStatistics());
  }

  @GetMapping("/statistics/detailed")
  @Operation(summary = "Детальная статистика по приоритетам")
  public ResponseEntity<List<Map<String, Object>>> getDetailedStatistics() {
    return ResponseEntity.ok(taskStatisticsService.getDetailedPriorityStatistics());
  }
}