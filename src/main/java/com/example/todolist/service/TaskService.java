package com.example.todolist.service;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

  private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

  private final TaskRepository taskRepository;

  private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

  @Autowired
  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
    logger.info("TaskService создан (конструктор)");
  }

  @PostConstruct
  public void initCache() {
    logger.info("@PostConstruct: Инициализация кэша задач...");

    List<Task> allTasks = taskRepository.findAll();
    allTasks.stream()
            .limit(3)
            .forEach(task -> taskCache.put(task.getId(), task));

    logger.info("Кэш инициализирован. Загружено задач: {}", taskCache.size());
    logger.info("Содержимое кэша: {}", taskCache.keySet());
  }

  @PreDestroy
  public void cleanup() {
    logger.info("@PreDestroy: Очистка ресурсов TaskService...");
    logger.info("Статистика перед завершением:");
    logger.info("Задач в кэше: {}", taskCache.size());
    logger.info("Всего задач в репозитории: {}", taskRepository.findAll().size());

    try (PrintWriter writer = new PrintWriter(new FileWriter("task-service-stats.txt", true))) {
      writer.printf("[%s] TaskService завершает работу.%n", LocalDateTime.now());
      writer.printf("Кэш содержал %d задач%n", taskCache.size());
      writer.printf("Всего задач в системе: %d%n", taskRepository.findAll().size());
      writer.println("------------------------");
      logger.info("Статистика сохранена в файл task-service-stats.txt");
    } catch (IOException e) {
      logger.error("Ошибка при сохранении статистики: {}", e.getMessage());
    }

    taskCache.clear();
    logger.info("Кэш очищен");
  }

  public List<Task> getAllTasks() {
    logger.debug("Получение всех задач");
    return taskRepository.findAll();
  }

  public Optional<Task> getTaskById(Long id) {
    logger.debug("Поиск задачи по ID: {}", id);

    Task cachedTask = taskCache.get(id);
    if (cachedTask != null) {
      logger.debug("Задача найдена в кэше");
      return Optional.of(cachedTask);
    }

    Optional<Task> task = taskRepository.findById(id);
    task.ifPresent(t -> {
      logger.debug("Задача найдена в репозитории, добавляем в кэш");
      taskCache.put(id, t);
    });

    return task;
  }

  public Task createTask(Task task) {
    logger.info("Создание новой задачи: {}", task.getTitle());
    Task createdTask = taskRepository.save(task);

    taskCache.put(createdTask.getId(), createdTask);
    logger.debug("Задача создана с ID: {}", createdTask.getId());

    return createdTask;
  }

  public Optional<Task> updateTask(Long id, Task taskDetails) {
    logger.info("Обновление задачи с ID: {}", id);

    Optional<Task> updatedTask = taskRepository.findById(id)
            .map(task -> {
              task.setTitle(taskDetails.getTitle());
              task.setDescription(taskDetails.getDescription());
              task.setCompleted(taskDetails.isCompleted());
              Task saved = taskRepository.save(task);

              taskCache.put(id, saved);
              logger.debug("Задача обновлена");

              return saved;
            });

    if (updatedTask.isEmpty()) {
      logger.warn("Задача с ID {} не найдена", id);
    }

    return updatedTask;
  }

  public boolean deleteTask(Long id) {
    logger.info("Удаление задачи с ID: {}", id);

    if (taskRepository.existsById(id)) {
      taskRepository.deleteById(id);
      taskCache.remove(id);
      logger.debug("Задача удалена");
      return true;
    }

    logger.warn("Задача с ID {} не найдена для удаления", id);
    return false;
  }

  public int getCacheSize() {
    return taskCache.size();
  }
}