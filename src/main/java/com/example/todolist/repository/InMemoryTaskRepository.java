package com.example.todolist.repository;

import com.example.todolist.exception.NotFoundTaskException;
import com.example.todolist.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class InMemoryTaskRepository implements TaskRepository {

  private final Map<Long, Task> storage = new ConcurrentHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(0);

  InMemoryTaskRepository() {
  }

  @Override
  public Task create(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Task must not be null");
    }
    Long id = idSequence.incrementAndGet();
    task.setId(id);
    storage.put(id, task);
    return task;
  }

  @Override
  public Optional<Task> findById(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public Task update(Task task) {
    if (task == null || task.getId() == null || !storage.containsKey(task.getId())) {
      throw new NotFoundTaskException(task == null ? null : task.getId());
    }
    storage.put(task.getId(), task);
    return task;
  }

  @Override
  public boolean deleteById(Long id) {
    if (id == null) {
      return false;
    }
    return storage.remove(id) != null;
  }
}