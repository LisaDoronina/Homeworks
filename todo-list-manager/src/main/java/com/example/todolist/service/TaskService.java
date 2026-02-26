package com.example.todolist.service;

import com.example.todolist.task.Task;
import com.example.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

  private final TaskRepository taskRepository;

  @Autowired
  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public List<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  public Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
  }

  public Task createTask(Task task) {
    return taskRepository.save(task);
  }

  public Optional<Task> updateTask(Long id, Task taskDetails) {
    return taskRepository.findById(id)
            .map(task -> {
              task.setTitle(taskDetails.getTitle());
              task.setDescription(taskDetails.getDescription());
              task.setCompleted(taskDetails.isCompleted());
              return taskRepository.save(task);
            });
  }

  public boolean deleteTask(Long id) {
    if (taskRepository.existsById(id)) {
      taskRepository.deleteById(id);
      return true;
    }
    return false;
  }
}