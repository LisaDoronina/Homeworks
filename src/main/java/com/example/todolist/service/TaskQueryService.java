package com.example.todolist.service;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskQueryService {

  private final TaskRepository taskRepository;

  public void demonstrateQueries() {
    log.info("=== Демонстрация работы репозиториев ===");

    List<Task> completedTasks = taskRepository.findByCompleted(true);
    log.info("Выполненные задачи: {}", completedTasks.size());

    List<Task> highPriorityTasks = taskRepository.findByPriority(Priority.HIGH);
    log.info("Задачи с высоким приоритетом: {}", highPriorityTasks.size());

    List<Task> pendingHighTasks = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);
    log.info("Невыполненные задачи с высоким приоритетом: {}", pendingHighTasks.size());

    List<Task> searchResults = taskRepository.findByTitleContainingIgnoreCase("задача");
    log.info("Результаты поиска по заголовку: {}", searchResults.size());

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime sevenDaysLater = now.plusDays(7);
    List<Task> upcomingTasks = taskRepository.findTasksDueWithinNextSevenDays(now, sevenDaysLater);
    log.info("Задачи на ближайшие 7 дней: {}", upcomingTasks.size());

    List<Task> overdueTasks = taskRepository.findOverdueTasks();
    log.info("Просроченные задачи: {}", overdueTasks.size());

    long totalTasks = taskRepository.count();
    long completedCount = taskRepository.countByCompleted(true);
    long urgentCount = taskRepository.countByPriority(Priority.URGENT);

    log.info("Всего задач: {}", totalTasks);
    log.info("Выполнено: {} ({}%)", completedCount,
            totalTasks > 0 ? (completedCount * 100 / totalTasks) : 0);
    log.info("Срочных задач: {}", urgentCount);
  }
}