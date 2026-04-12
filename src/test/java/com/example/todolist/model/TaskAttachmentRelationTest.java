package com.example.todolist.model;

import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class TaskAttachmentRelationTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TaskAttachmentRepository attachmentRepository;

  @Test
  void testTaskAttachmentRelationship() {
    Task task = new Task();
    task.setTitle("Тестовая задача");
    task.setPriority(Priority.MEDIUM);
    task = taskRepository.save(task);

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTask(task);
    attachment.setFileName("test.txt");
    attachment.setStoredFileName("uuid-test.txt");
    attachment.setContentType("text/plain");
    attachment.setSize(512L);
    attachment.setUploadedAt(LocalDateTime.now());
    attachment = attachmentRepository.save(attachment);

    Optional<TaskAttachment> found = attachmentRepository.findById(attachment.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getTask().getId()).isEqualTo(task.getId());

    assertThat(attachmentRepository.findByTaskId(task.getId())).hasSize(1);
  }

  @Test
  void testSaveTaskWithAttachments() {
    Task task = new Task();
    task.setTitle("Задача с вложениями");
    task.setPriority(Priority.HIGH);
    task = taskRepository.save(task);

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTask(task);
    attachment.setFileName("document.pdf");
    attachment.setStoredFileName("uuid-doc.pdf");
    attachment.setContentType("application/pdf");
    attachment.setSize(1024L);
    attachment.setUploadedAt(LocalDateTime.now());
    attachment = attachmentRepository.save(attachment);

    Optional<TaskAttachment> found = attachmentRepository.findById(attachment.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getTask().getId()).isEqualTo(task.getId());
  }

  @Test
  void testAddAttachmentToExistingTask() {
    Task task = new Task();
    task.setTitle("Существующая задача");
    task.setPriority(Priority.MEDIUM);
    task = taskRepository.save(task);

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTask(task);
    attachment.setFileName("new-file.pdf");
    attachment.setStoredFileName("uuid-new.pdf");
    attachment.setContentType("application/pdf");
    attachment.setSize(3072L);
    attachment.setUploadedAt(LocalDateTime.now());
    attachment = attachmentRepository.save(attachment);

    Optional<TaskAttachment> foundAttachment = attachmentRepository.findById(attachment.getId());
    assertThat(foundAttachment).isPresent();
    assertThat(foundAttachment.get().getTask().getId()).isEqualTo(task.getId());

    assertThat(attachmentRepository.findByTaskId(task.getId())).hasSize(1);
  }
}