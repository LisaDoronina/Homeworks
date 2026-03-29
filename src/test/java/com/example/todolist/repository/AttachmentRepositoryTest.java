package com.example.todolist.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.todolist.exception.AttachmentNotFoundException;
import com.example.todolist.model.TaskAttachment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttachmentRepositoryTest {

  private InMemoryTaskAttachmentRepository repository;

  @BeforeEach
  void init() {
    repository = new InMemoryTaskAttachmentRepository();
  }

  @Test
  void create_shouldAssignIdAndStoreAttachment() {

    TaskAttachment attachment = createAttachment(null, 10L, "notes.pdf");

    TaskAttachment created = repository.create(attachment);

    assertNotNull(created.getId());
    assertEquals(1L, created.getId());
    assertTrue(repository.findById(created.getId()).isPresent());
    assertSame(created, repository.findById(created.getId()).get());
  }

  @Test
  void shouldReturnOnlyAttachmentsForGivenTask() {
    TaskAttachment task10First = repository.create(createAttachment(null, 10L, "first.txt"));
    TaskAttachment task10Second = repository.create(createAttachment(null, 10L, "second.txt"));
    repository.create(createAttachment(null, 20L, "third.txt"));

    List<TaskAttachment> attachments = repository.findAllAttachmentsByTaskId(10L);

    assertEquals(2, attachments.size());

    Set<Long> actualIds = attachments.stream()
            .map(TaskAttachment::getId)
            .collect(Collectors.toSet());

    Set<Long> expectedIds = Set.of(task10First.getId(), task10Second.getId());
    assertEquals(expectedIds, actualIds);
  }

  @Test
  void shouldReplaceExistingAttachmentWhenIdFound() {
    TaskAttachment created = repository.create(createAttachment(null, 10L, "draft.txt"));
    TaskAttachment updated = createAttachment(created.getId(), 10L, "final.txt");

    TaskAttachment result = repository.update(updated);

    assertSame(updated, result);

    TaskAttachment stored = repository.findById(created.getId()).orElseThrow();
    assertEquals("final.txt", stored.getFileName());
  }

  @Test
  void shouldThrowExceptionWhenAttachmentNotFound() {
    TaskAttachment missing = createAttachment(999L, 10L, "missing.txt");

    assertThrows(AttachmentNotFoundException.class,
            () -> repository.update(missing));
  }

  @Test
  void shouldRemoveExistingAttachment() {

    TaskAttachment created = repository.create(createAttachment(null, 10L, "notes.pdf"));

    boolean deleted = repository.deleteById(created.getId());

    assertTrue(deleted);
    assertFalse(repository.findById(created.getId()).isPresent());
  }

  @Test
  void shouldReturnFalseWhenAttachmentDoesNotExist() {

    boolean deleted = repository.deleteById(999L);

    assertFalse(deleted);
  }

  private TaskAttachment createAttachment(Long id, Long taskId, String fileName) {
    return TaskAttachment.builder()
            .id(id)
            .taskId(taskId)
            .fileName(fileName)
            .storedFileName("stored-" + fileName)
            .contentType("text/plain")
            .size(123L)
            .uploadedAt(LocalDateTime.of(2026, 3, 21, 12, 0))
            .build();
  }
}