package com.example.todolist.service;

import com.example.todolist.exception.BulkOperationException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskService {

  private final TaskRepository taskRepository;
  private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

  @Autowired
  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
    log.info("TaskService создан (конструктор)");
  }

  @PostConstruct
  public void initCache() {
    log.info("@PostConstruct: Инициализация кэша задач...");
    List<Task> allTasks = taskRepository.findAll();
    allTasks.stream()
            .limit(3)
            .forEach(task -> taskCache.put(task.getId(), task));
    log.info("Кэш инициализирован. Загружено задач: {}", taskCache.size());
    log.info("Содержимое кэша: {}", taskCache.keySet());
  }

  @PreDestroy
  public void cleanup() {
    log.info("@PreDestroy: Очистка ресурсов TaskService...");
    log.info("Статистика перед завершением:");
    log.info("Задач в кэше: {}", taskCache.size());
    log.info("Всего задач в репозитории: {}", taskRepository.findAll().size());

    try (PrintWriter writer = new PrintWriter(new FileWriter("task-service-stats.txt", true))) {
      writer.printf("[%s] TaskService завершает работу.%n", LocalDateTime.now());
      writer.printf("Кэш содержал %d задач%n", taskCache.size());
      writer.printf("Всего задач в системе: %d%n", taskRepository.findAll().size());
      writer.println("------------------------");
      log.info("Статистика сохранена в файл task-service-stats.txt");
    } catch (IOException e) {
      log.error("Ошибка при сохранении статистики: {}", e.getMessage());
    }

    taskCache.clear();
    log.info("Кэш очищен");
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<Task> getAllTasks() {
    log.debug("Получение всех задач");
    return taskRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Task> getAllTasksWithAttachments() {
    log.debug("Получение всех задач с вложениями (используя EntityGraph)");
    return taskRepository.findAllWithAttachments();
  }

  @Transactional(readOnly = true)
  public Optional<Task> getTaskByIdWithAttachments(Long id) {
    log.debug("Поиск задачи с вложениями по ID: {}", id);
    return taskRepository.findByIdWithAttachments(id);
  }

  @Transactional(readOnly = true)
  public Optional<Task> getTaskById(Long id) {
    log.debug("Поиск задачи по ID: {}", id);

    Task cachedTask = taskCache.get(id);
    if (cachedTask != null) {
      log.debug("Задача найдена в кэше");
      Hibernate.initialize(cachedTask.getAttachments());
      return Optional.of(cachedTask);
    }

    Optional<Task> task = taskRepository.findByIdWithAttachments(id);
    task.ifPresent(t -> {
      log.debug("Задача найдена в репозитории, добавляем в кэш");
      taskCache.put(id, t);
    });

    return task;
  }

  @Transactional(
          propagation = Propagation.REQUIRED,
          isolation = Isolation.READ_COMMITTED,
          rollbackFor = Exception.class,
          timeout = 30
  )
  public Task createTask(Task task) {
    log.info("Создание новой задачи: {}", task.getTitle());

    LocalDateTime now = LocalDateTime.now();
    if (task.getCreatedAt() == null) {
      task.setCreatedAt(now);
    }
    if (task.getUpdatedAt() == null) {
      task.setUpdatedAt(now);
    }

    Task createdTask = taskRepository.save(task);
    taskCache.put(createdTask.getId(), createdTask);
    log.debug("Задача создана с ID: {}", createdTask.getId());

    return createdTask;
  }

  @Transactional(
          propagation = Propagation.REQUIRED,
          isolation = Isolation.READ_COMMITTED,
          rollbackFor = {TaskNotFoundException.class, Exception.class}
  )
  public Task updateTask(Long id, Task taskDetails) {
    log.info("Обновление задачи с ID: {}", id);

    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

    if (taskDetails.getTitle() != null) {
      task.setTitle(taskDetails.getTitle());
    }
    if (taskDetails.getDescription() != null) {
      task.setDescription(taskDetails.getDescription());
    }
    task.setCompleted(taskDetails.isCompleted());
    if (taskDetails.getDueDate() != null) {
      task.setDueDate(taskDetails.getDueDate());
    }
    if (taskDetails.getPriority() != null) {
      task.setPriority(taskDetails.getPriority());
    }
    if (taskDetails.getTags() != null) {
      task.setTags(taskDetails.getTags());
    }

    Task saved = taskRepository.save(task);
    taskCache.put(id, saved);
    log.debug("Задача обновлена");

    return saved;
  }

  @Transactional(
          propagation = Propagation.REQUIRED,
          rollbackFor = Exception.class
  )
  public boolean deleteTask(Long id) {
    log.info("Удаление задачи с ID: {}", id);

    if (taskRepository.existsById(id)) {
      taskRepository.deleteById(id);
      taskCache.remove(id);
      log.debug("Задача удалена");
      return true;
    }

    log.warn("Задача с ID {} не найдена для удаления", id);
    return false;
  }

  @Transactional(
          propagation = Propagation.REQUIRED,
          isolation = Isolation.REPEATABLE_READ,
          rollbackFor = {BulkOperationException.class, RuntimeException.class},
          timeout = 60,
          readOnly = false
  )
  public int bulkCompleteTasks(List<Long> ids) {
    log.info("Массовое обновление статуса задач. IDs: {}", ids);

    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("Список ID не может быть пустым");
    }

    Set<Long> existingIds = taskRepository.findAllById(ids).stream()
            .map(Task::getId)
            .collect(Collectors.toSet());

    List<Long> missingIds = ids.stream()
            .filter(id -> !existingIds.contains(id))
            .collect(Collectors.toList());

    if (!missingIds.isEmpty()) {
      log.error("Задачи не найдены: {}", missingIds);
      throw new BulkOperationException(
              "Некоторые задачи не найдены. Операция отменена.",
              missingIds
      );
    }

    int updatedCount = taskRepository.bulkCompleteTasks(ids);

    ids.forEach(id -> {
      taskRepository.findById(id).ifPresent(task -> {
        taskCache.put(id, task);
        log.debug("Кэш обновлён для задачи ID: {}", id);
      });
    });

    log.info("Успешно обновлено {} задач", updatedCount);
    return updatedCount;
  }

  @Transactional(
          propagation = Propagation.REQUIRED,
          isolation = Isolation.READ_COMMITTED,
          rollbackFor = Exception.class
  )
  public int bulkDeleteTasks(List<Long> ids) {
    log.info("Массовое удаление задач. IDs: {}", ids);

    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("Список ID не может быть пустым");
    }

    Set<Long> existingIds = taskRepository.findAllById(ids).stream()
            .map(Task::getId)
            .collect(Collectors.toSet());

    List<Long> missingIds = ids.stream()
            .filter(id -> !existingIds.contains(id))
            .collect(Collectors.toList());

    if (!missingIds.isEmpty()) {
      throw new BulkOperationException(
              "Некоторые задачи не найдены. Операция отменена.",
              missingIds
      );
    }

    int deletedCount = taskRepository.bulkDeleteTasks(ids);

    ids.forEach(taskCache::remove);

    log.info("Успешно удалено {} задач", deletedCount);
    return deletedCount;
  }

  public int getCacheSize() {
    return taskCache.size();
  }

  public void evictCache() {
    taskCache.clear();
  }
}