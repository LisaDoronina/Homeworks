package com.example.todolist.service;

import com.example.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TaskStatisticsService {

  private final TaskRepository primaryRepository;
  private final TaskRepository stubRepository;

  @Autowired
  public TaskStatisticsService(
          TaskRepository primaryRepository,
          @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
    this.primaryRepository = primaryRepository;
    this.stubRepository = stubRepository;
  }

  public String compareRepositories() {
    StringBuilder result = new StringBuilder();
    result.append("=== Сравнение репозиториев ===\n");
    result.append(String.format("Основной репозиторий (%s): %d задач\n",
            primaryRepository.getClass().getSimpleName(),
            primaryRepository.findAll().size()));
    result.append(String.format("Stub репозиторий (%s): %d задач\n",
            stubRepository.getClass().getSimpleName(),
            stubRepository.findAll().size()));
    result.append("==============================");

    System.out.println(result.toString());

    return result.toString();
  }

  public String getRepositoryInfo() {
    return String.format(
            "<h3>Информация о репозиториях</h3>" +
                    "<p><strong>Основной репозиторий (%s):</strong> %d задач</p>" +
                    "<p><strong>Stub репозиторий:</strong> %d задач</p>" +
                    "<p><small>Основной репозиторий внедрен через @Primary, " +
                    "Stub - через @Qualifier(\"stubTaskRepository\")</small></p>",
            primaryRepository.getClass().getSimpleName(),
            primaryRepository.findAll().size(),
            stubRepository.findAll().size()
    );
  }
}