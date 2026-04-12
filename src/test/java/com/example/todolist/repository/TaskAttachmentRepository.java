package com.example.todolist.repository;

import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskAttachmentRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TaskAttachmentRepository attachmentRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Task task;
  private TaskAttachment attachment1;
  private TaskAttachment attachment2;

  @BeforeEach
  void setUp() {
    attachmentRepository.deleteAll();
    taskRepository.deleteAll();

    task = new Task();
    task.setTitle("Задача с вложениями");
    task.setDescription("Тестовая задача");
    task.setPriority(com.example.todolist.model.Priority.HIGH);
    task = taskRepository.save(task);

    attachment1 = new TaskAttachment();
    attachment1.setTask(task);
    attachment1.setFileName("file1.pdf");
    attachment1.setStoredFileName("uuid-file1.pdf");
    attachment1.setContentType("application/pdf");
    attachment1.setSize(1024L);
    attachment1.setUploadedAt(LocalDateTime.now());

    attachment2 = new TaskAttachment();
    attachment2.setTask(task);
    attachment2.setFileName("file2.jpg");
    attachment2.setStoredFileName("uuid-file2.jpg");
    attachment2.setContentType("image/jpeg");
    attachment2.setSize(2048L);
    attachment2.setUploadedAt(LocalDateTime.now());

    attachmentRepository.saveAll(List.of(attachment1, attachment2));
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void testFindByTask() {
    Task foundTask = taskRepository.findById(task.getId()).get();
    List<TaskAttachment> attachments = attachmentRepository.findByTask(foundTask);

    assertThat(attachments).hasSize(2);
    assertThat(attachments).extracting(TaskAttachment::getFileName)
            .containsExactlyInAnyOrder("file1.pdf", "file2.jpg");
  }

  @Test
  void testFindByTaskId() {
    List<TaskAttachment> attachments = attachmentRepository.findByTaskId(task.getId());

    assertThat(attachments).hasSize(2);
    assertThat(attachments.get(0).getTask().getId()).isEqualTo(task.getId());
  }

  @Test
  void testCountByTaskId() {
    long count = attachmentRepository.countByTaskId(task.getId());
    assertThat(count).isEqualTo(2);
  }

  @Test
  void testFindByContentTypeContaining() {
    List<TaskAttachment> pdfAttachments = attachmentRepository.findByContentTypeContaining("pdf");
    assertThat(pdfAttachments).hasSize(1);
    assertThat(pdfAttachments.get(0).getFileName()).isEqualTo("file1.pdf");
  }

  @Test
  void testDeleteByTaskId() {
    attachmentRepository.deleteByTaskId(task.getId());
    entityManager.flush();

    List<TaskAttachment> remaining = attachmentRepository.findByTaskId(task.getId());
    assertThat(remaining).isEmpty();
  }

  @Test
  void testCascadeDelete() {
    taskRepository.deleteById(task.getId());
    entityManager.flush();

    List<TaskAttachment> attachments = attachmentRepository.findAll();
    assertThat(attachments).isEmpty();
  }
}