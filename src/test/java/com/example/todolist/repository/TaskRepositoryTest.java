package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @BeforeEach
  void setUp() {
    Task task1 = new Task();
    task1.setTitle("Срочная задача");
    task1.setPriority(Priority.URGENT);
    task1.setCompleted(false);
    task1.setDueDate(LocalDateTime.now().plusDays(2));

    Task task2 = new Task();
    task2.setTitle("Высокий приоритет");
    task2.setPriority(Priority.HIGH);
    task2.setCompleted(false);
    task2.setDueDate(LocalDateTime.now().plusDays(5));

    Task task3 = new Task();
    task3.setTitle("Выполненная задача");
    task3.setPriority(Priority.MEDIUM);
    task3.setCompleted(true);
    task3.setDueDate(LocalDateTime.now().minusDays(1));

    taskRepository.saveAll(List.of(task1, task2, task3));
  }

  @Test
  void testFindByCompleted() {
    List<Task> completedTasks = taskRepository.findByCompleted(true);
    assertThat(completedTasks).hasSize(1);
    assertThat(completedTasks.get(0).getTitle()).isEqualTo("Выполненная задача");
  }

  @Test
  void testFindByPriority() {
    List<Task> urgentTasks = taskRepository.findByPriority(Priority.URGENT);
    assertThat(urgentTasks).hasSize(1);
    assertThat(urgentTasks.get(0).getTitle()).isEqualTo("Срочная задача");
  }

  @Test
  void testFindByCompletedAndPriority() {
    List<Task> pendingHighTasks = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);
    assertThat(pendingHighTasks).hasSize(1);
    assertThat(pendingHighTasks.get(0).getTitle()).isEqualTo("Высокий приоритет");
  }

  @Test
  void testFindTasksDueWithinNextSevenDays() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime sevenDaysLater = now.plusDays(7);
    List<Task> upcomingTasks = taskRepository.findTasksDueWithinNextSevenDays(now, sevenDaysLater);
    assertThat(upcomingTasks).hasSize(2);
  }

  @Test
  void testFindOverdueTasks() {
    List<Task> overdueTasks = taskRepository.findOverdueTasks();
    assertThat(overdueTasks).isEmpty();
  }
}