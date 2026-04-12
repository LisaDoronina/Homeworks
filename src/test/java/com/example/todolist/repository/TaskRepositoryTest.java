package com.example.todolist.repository;

import com.example.todolist.model.Priority;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TaskAttachmentRepository attachmentRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Task task1;
  private Task task2;
  private Task task3;

  @BeforeEach
  void setUp() {
    attachmentRepository.deleteAll();
    taskRepository.deleteAll();

    task1 = new Task();
    task1.setTitle("Срочная задача");
    task1.setDescription("Описание срочной задачи");
    task1.setCompleted(false);
    task1.setPriority(Priority.URGENT);
    task1.setDueDate(LocalDateTime.now().plusDays(2));
    task1.setTagSet("[urgent, important]");

    task2 = new Task();
    task2.setTitle("Высокий приоритет");
    task2.setDescription("Описание высокой задачи");
    task2.setCompleted(false);
    task2.setPriority(Priority.HIGH);
    task2.setDueDate(LocalDateTime.now().plusDays(5));
    task2.setTagSet("[high, priority]");

    task3 = new Task();
    task3.setTitle("Выполненная задача");
    task3.setDescription("Уже выполнено");
    task3.setCompleted(true);
    task3.setPriority(Priority.MEDIUM);
    task3.setDueDate(LocalDateTime.now().minusDays(1));
    task3.setTagSet("[done]");

    taskRepository.saveAll(List.of(task1, task2, task3));
    entityManager.flush();
    entityManager.clear();
  }


  @Test
  void testFindAll() {
    List<Task> tasks = taskRepository.findAll();
    assertThat(tasks).hasSize(3);
  }

  @Test
  void testFindById() {
    Optional<Task> found = taskRepository.findById(task1.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getTitle()).isEqualTo("Срочная задача");
  }

  @Test
  void testSaveTask() {
    Task newTask = new Task();
    newTask.setTitle("Новая задача");
    newTask.setDescription("Тестовая задача");
    newTask.setPriority(Priority.LOW);

    Task saved = taskRepository.save(newTask);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getTitle()).isEqualTo("Новая задача");
  }

  @Test
  void testDeleteTask() {
    taskRepository.deleteById(task1.getId());

    Optional<Task> found = taskRepository.findById(task1.getId());
    assertThat(found).isEmpty();
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
  void testFindByDueDateBefore() {
    List<Task> overdueTasks = taskRepository.findByDueDateBefore(LocalDateTime.now());
    assertThat(overdueTasks).hasSize(1);
    assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Выполненная задача");
  }

  @Test
  void testFindByTitleContainingIgnoreCase() {
    List<Task> found = taskRepository.findByTitleContainingIgnoreCase("срочная");
    assertThat(found).hasSize(1);
    assertThat(found.get(0).getTitle()).isEqualTo("Срочная задача");
  }

  @Test
  void testFindByTagsContaining() {
    List<Task> tasksWithUrgentTag = taskRepository.findByTagsContaining("urgent");
    assertThat(tasksWithUrgentTag).hasSize(1);
  }


  @Test
  void testFindTasksDueWithinNextSevenDays() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime sevenDaysLater = now.plusDays(7);

    List<Task> upcomingTasks = taskRepository.findTasksDueWithinNextSevenDays(now, sevenDaysLater);

    assertThat(upcomingTasks).hasSize(2);
    assertThat(upcomingTasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Срочная задача", "Высокий приоритет");
  }

  @Test
  void testFindOverdueTasks() {
    List<Task> overdueTasks = taskRepository.findOverdueTasks();
    assertThat(overdueTasks).isEmpty();
  }

  @Test
  void testFindByPriorityOrderByDueDate() {
    List<Task> tasks = taskRepository.findByPriorityOrderByDueDate(Priority.HIGH);
    assertThat(tasks).hasSize(1);
  }


  @Test
  void testTagsConversion() {
    Task task = new Task();
    task.setTitle("Задача с тегами");
    task.setPriority(Priority.MEDIUM);
    task.setTags("tag1,tag2,tag3");

    Task saved = taskRepository.save(task);
    entityManager.flush();
    entityManager.clear();

    Task found = taskRepository.findById(saved.getId()).get();

    assertThat(found.getTagSet()).containsExactlyInAnyOrder("tag1", "tag2", "tag3");
  }
}