package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.service.*;
import com.example.todolist.validation.OnCreate;
import com.example.todolist.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Tasks", description = "Операции с задачами")
@RestController
@RequestMapping("/api/tasks")
public abstract class TaskController {

  private final TaskService taskService;
  private final TaskStatisticsService statisticsService;
  private final AppInfoService appInfoService;
  private final RequestScopedBean requestScopedBean;
  private final TaskMapper taskMapper;

  @Value("${api.version:2.0.0}")
  private String apiVersion;

  @Autowired
  public TaskController(TaskService taskService,
                        TaskStatisticsService statisticsService,
                        AppInfoService appInfoService,
                        RequestScopedBean requestScopedBean,
                        TaskMapper taskMapper) {
    this.taskService = taskService;
    this.statisticsService = statisticsService;
    this.appInfoService = appInfoService;
    this.requestScopedBean = requestScopedBean;
    this.taskMapper = taskMapper;
  }

  @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач с заголовком X-Total-Count")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class)))
  })
  @GetMapping
  public ResponseEntity<List<TaskResponseDto>> getAllTasks(
          @CookieValue(name = "viewPreference", defaultValue = "detailed") String viewPreference) {

    List<TaskResponseDto> tasks = taskService.getAllTasks()
            .stream()
            .map(taskMapper::toResponseDto)
            .collect(Collectors.toList());

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(tasks.size()));
    headers.add("X-API-Version", apiVersion);

    return ResponseEntity.ok().headers(headers).body(tasks);
  }

  @Operation(summary = "Получить задачу по ID")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Задача получена",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))),
          @ApiResponse(responseCode = "404", description = "Задача не найдена")
  })
  @GetMapping("/{id}")
  public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
    return taskService.getTaskById(id)
            .map(taskMapper::toResponseDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Создать новую задачу")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "Задача создана",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))),
          @ApiResponse(responseCode = "400", description = "Ошибка валидации")
  })
  @PostMapping
  public ResponseEntity<TaskResponseDto> createTask(
          @Validated(OnCreate.class) @RequestBody TaskCreateDto taskCreateDto) {

    PrototypeScopedBean prototypeBean = getPrototypeScopedBean();
    String generatedId = prototypeBean.generateTaskId();

    Task taskEntity = taskMapper.toEntity(taskCreateDto);
    taskEntity.setCreatedAt(java.time.LocalDateTime.now());

    Task createdTask = taskService.createTask(taskEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Version", apiVersion);

    return ResponseEntity.status(HttpStatus.CREATED)
            .headers(headers)
            .body(taskMapper.toResponseDto(createdTask));
  }

  @Operation(summary = "Обновить задачу по ID")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Задача обновлена",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))),
          @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
          @ApiResponse(responseCode = "404", description = "Задача не найдена")
  })
  @PutMapping("/{id}")
  public ResponseEntity<TaskResponseDto> updateTask(
          @PathVariable Long id,
          @Validated(OnUpdate.class) @RequestBody TaskUpdateDto taskUpdateDto) {

    Task existingTask = taskService.getTaskById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

    if (taskUpdateDto.getDueDate() != null &&
            taskUpdateDto.getDueDate().isBefore(existingTask.getCreatedAt())) {
      throw new IllegalArgumentException("dueDate не может быть раньше createdAt");
    }

    taskMapper.updateEntity(taskUpdateDto, existingTask);
    Task updatedTask = taskService.updateTask(id, existingTask).orElseThrow();

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Version", apiVersion);

    return ResponseEntity.ok().headers(headers).body(taskMapper.toResponseDto(updatedTask));
  }

  @Operation(summary = "Удалить задачу по ID")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "Задача удалена"),
          @ApiResponse(responseCode = "404", description = "Задача не найдена")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    if (taskService.deleteTask(id)) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("X-API-Version", apiVersion);
      return ResponseEntity.noContent().headers(headers).build();
    }
    return ResponseEntity.notFound().build();
  }

  @Operation(summary = "Получить статистику репозиториев задач")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Статистика получена")
  })
  @GetMapping("/stats/repositories")
  public ResponseEntity<String> getRepositoryStats() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Version", apiVersion);
    return ResponseEntity.ok().headers(headers).body(statisticsService.getRepositoryInfo());
  }

  @Operation(summary = "Получить размер кэша задач")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Размер кэша получен")
  })
  @GetMapping("/cache/size")
  public ResponseEntity<String> getCacheSize() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Version", apiVersion);
    return ResponseEntity.ok().headers(headers).body(String.format("Размер кэша задач: %d", taskService.getCacheSize()));
  }

  @Operation(summary = "Получить информацию о приложении")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Информация получена")
  })
  @GetMapping("/info/app")
  public ResponseEntity<String> getAppInfo() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Version", apiVersion);
    return ResponseEntity.ok().headers(headers).body(appInfoService.getAppInfo());
  }

  @Operation(summary = "Демонстрация Prototype Scope")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Success")
  })
  @GetMapping("/info/prototype")
  public ResponseEntity<String> getPrototypeInfo() {
    PrototypeScopedBean bean1 = getPrototypeScopedBean();
    PrototypeScopedBean bean2 = getPrototypeScopedBean();

    String body = String.format(
            "<h3>Демонстрация Prototype Scope</h3>" +
                    "<p><strong>Бин 1:</strong> %s</p>" +
                    "<p><strong>Бин 2:</strong> %s</p>" +
                    "<p><strong>Равны ли бины?</strong> %s</p>" +
                    "<p><small>Каждый вызов getPrototypeScopedBean() создает новый экземпляр</small></p>",
            bean1.getBeanInfo(),
            bean2.getBeanInfo(),
            (bean1 == bean2) ? "НЕТ (это разные объекты)" : "ДА (одинаковые)"
    );

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Version", apiVersion);
    return ResponseEntity.ok().headers(headers).body(body);
  }

  @Lookup
  protected abstract PrototypeScopedBean getPrototypeScopedBean();
}