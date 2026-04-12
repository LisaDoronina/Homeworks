package com.example.todolist.repository;

import com.example.todolist.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class StubTaskRepository implements TaskRepository {

  private final Map<Long, Task> tasks = new ConcurrentHashMap<>();

  public StubTaskRepository() {
    Task task1 = new Task("Купить продукты", "Молоко, хлеб, яйца, овощи");
    Task task2 = new Task("Сделать домашнее задание", "Spring Framework: To-Do List Manager MVP");
    Task task3 = new Task("Позвонить родителям", "Узнать как дела, поздравить с праздником");
    Task task4 = new Task("Сходить в спортзал", "Тренировка спины и ног");
    Task task5 = new Task("Почитать книгу", "Clean Code - Роберт Мартин");

    tasks.put(1L, task1);
    tasks.put(2L, task2);
    tasks.put(3L, task3);
    tasks.put(4L, task4);
    tasks.put(5L, task5);
  }

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(tasks.values());
  }

  @Override
  public Optional<Task> findById(Long id) {
    return Optional.ofNullable(tasks.get(id));
  }

  @Override
  public Task save(Task task) {
    if (task.getId() == null) {
      long newId = tasks.keySet().stream()
              .mapToLong(Long::longValue)
              .max()
              .orElse(0L) + 1;
      task.setId(newId);
    }
    tasks.put(task.getId(), task);
    return task;
  }

  @Override
  public void deleteById(Long id) {
    tasks.remove(id);
  }

  @Override
  public boolean existsById(Long id) {
    return tasks.containsKey(id);
  }
}