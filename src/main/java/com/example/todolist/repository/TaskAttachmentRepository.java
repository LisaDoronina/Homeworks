package com.example.todolist.repository;

import com.example.todolist.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskAttachmentRepository {

  private final Map<Long, TaskAttachment> storage = new HashMap<>();
  private final AtomicLong counter = new AtomicLong(1);

  public TaskAttachment save(TaskAttachment attachment) {
    if (attachment.getId() == null) {
      attachment.setId(counter.getAndIncrement());
    }
    storage.put(attachment.getId(), attachment);
    return attachment;
  }

  public Optional<TaskAttachment> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<TaskAttachment> findByTaskId(Long taskId) {
    List<TaskAttachment> result = new ArrayList<>();
    for (TaskAttachment a : storage.values()) {
      if (a.getId().equals(taskId)) {
        result.add(a);
      }
    }
    return result;
  }

  public boolean delete(Long id) {
    return storage.remove(id) != null;
  }
}