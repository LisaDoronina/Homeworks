package com.example.todolist.service;

import com.example.todolist.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskStatisticsService {

  private final TaskRepository taskRepository;

  public TaskStatisticsService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public String getRepositoryInfo() {
    return String.format(
            "<h3>Информация о репозитории</h3>" +
                    "<p><strong>Репозиторий (%s):</strong> %d задач</p>",
            taskRepository.getClass().getSimpleName(),
            taskRepository.findAll().size()
    );
  }

  public long getTotalTasksCount() {
    return taskRepository.count();
  }

  public String compareRepositories() {
    return "Используется JPA репозиторий с PostgreSQL";
  }
}