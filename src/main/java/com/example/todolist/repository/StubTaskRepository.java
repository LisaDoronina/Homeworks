package com.example.todolist.repository;

import com.example.todolist.model.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StubTaskRepository implements TaskRepository {

  private final List<Task> tasks;

  public StubTaskRepository() {
    List<Task> seed = new ArrayList<>();

    Task task1 = new Task(1L, "Sample task", "Stub repository task", false);
    seed.add(task1);

    Task task2 = new Task(2L, "Done task", "Completed stub task", true);
    seed.add(task2);

    this.tasks = Collections.unmodifiableList(seed);
  }

  @Override
  public Task create(Task task) {
    throw new UnsupportedOperationException("Stub repository is read-only");
  }

  @Override
  public Optional<Task> findById(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    return tasks.stream()
            .filter(task -> Objects.equals(task.getId(), id))
            .findFirst();
  }

  @Override
  public List<Task> findAll() {
    return tasks;
  }

  @Override
  public Task update(Task task) {
    throw new UnsupportedOperationException("Stub repository is read-only");
  }

  @Override
  public boolean deleteById(Long id) {
    throw new UnsupportedOperationException("Stub repository is read-only");
  }
}