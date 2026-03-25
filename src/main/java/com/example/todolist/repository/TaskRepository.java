package com.example.todolist.repository;

import com.example.todolist.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {

  List<Task> findAll();

  Optional<Task> findById(Long id);

  boolean deleteById(Long id);

  Task create(Task task);

  Task update(Task task);
}