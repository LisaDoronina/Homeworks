package com.example.todolist.service;

import com.example.todolist.exception.BulkOperationException;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceIntegrationTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  private Task testTask;

  @BeforeEach
  void setUp() {
    taskRepository.deleteAll();

    testTask = new Task();
    testTask.setTitle("Тестовая задача");
    testTask.setDescription("Описание");
    testTask.setPriority(Priority.HIGH);
    testTask.setDueDate(LocalDateTime.now().plusDays(7));
    testTask.setTagSet("[test, important]");

    testTask = taskService.createTask(testTask);
  }

  @Test
  void testCreateTask() {
    Task newTask = new Task();
    newTask.setTitle("Новая задача");
    newTask.setDescription("Новое описание");
    newTask.setPriority(Priority.URGENT);

    Task created = taskService.createTask(newTask);

    assertThat(created.getId()).isNotNull();
    assertThat(created.getTitle()).isEqualTo("Новая задача");
    assertThat(created.getCreatedAt()).isNotNull();
    assertThat(created.getUpdatedAt()).isNotNull();
  }

  @Test
  void testGetTaskById() {
    Optional<Task> found = taskService.getTaskById(testTask.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getTitle()).isEqualTo("Тестовая задача");
  }

  @Test
  void testUpdateTask() {
    Task updateDetails = new Task();
    updateDetails.setTitle("Обновлённая задача");
    updateDetails.setCompleted(true);

    Task updated = taskService.updateTask(testTask.getId(), updateDetails);

    assertThat(updated.getTitle()).isEqualTo("Обновлённая задача");
    assertThat(updated.isCompleted()).isTrue();
  }

  @Test
  void testDeleteTask() {
    boolean deleted = taskService.deleteTask(testTask.getId());

    assertThat(deleted).isTrue();
    assertThat(taskRepository.findById(testTask.getId())).isEmpty();
  }

  @Test
  void testBulkCompleteTasks_RollbackOnMissingId() {
    List<Long> ids = List.of(testTask.getId(), 99999L, 88888L);

    assertThatThrownBy(() -> taskService.bulkCompleteTasks(ids))
            .isInstanceOf(BulkOperationException.class)
            .hasMessageContaining("Некоторые задачи не найдены");

    Optional<Task> task = taskService.getTaskById(testTask.getId());
    assertThat(task.get().isCompleted()).isFalse();
  }
}