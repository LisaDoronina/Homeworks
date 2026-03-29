package com.example.todolist.repository;

import com.example.todolist.exception.AttachmentNotFoundException;
import com.example.todolist.model.TaskAttachment;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

  private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(0);

  InMemoryTaskAttachmentRepository() {
  }

  @Override
  public TaskAttachment create(TaskAttachment attachment) {
    if (attachment == null) {
      throw new IllegalArgumentException("Attachment must not be null");
    }
    Long id = idSequence.incrementAndGet();
    attachment.setId(id);
    storage.put(id, attachment);
    return attachment;
  }

  @Override
  public Optional<TaskAttachment> findById(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<TaskAttachment> findAllAttachmentsByTaskId(Long taskId) {
    if (taskId == null) {
      return List.of();
    }
    return storage.values().stream()
            .filter(attachment -> Objects.equals(taskId, attachment.getTaskId()))
            .toList();
  }

  @Override
  public TaskAttachment update(TaskAttachment attachment) {
    if (attachment == null) {
      throw new IllegalArgumentException("Attachment must not be null");
    }
    if (attachment.getId() == null
            || !storage.containsKey(attachment.getId())) {
      throw new AttachmentNotFoundException(attachment.getId());
    }
    storage.put(attachment.getId(), attachment);
    return attachment;
  }

  @Override
  public boolean deleteById(Long id) {
    if (id == null) {
      return false;
    }
    return storage.remove(id) != null;
  }
}