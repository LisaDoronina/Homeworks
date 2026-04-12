package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByCompleted(boolean completed);
  List<Task> findByPriority(Priority priority);
  List<Task> findByCompletedAndPriority(boolean completed, Priority priority);
  List<Task> findByDueDateBefore(LocalDateTime dueDate);
  List<Task> findByDueDateAfter(LocalDateTime dueDate);
  List<Task> findByTitleContainingIgnoreCase(String title);
  List<Task> findByTagsContaining(String tag);
  long countByCompleted(boolean completed);
  long countByPriority(Priority priority);

  @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.attachments")
  List<Task> findAllWithAttachments();

  @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.attachments WHERE t.id = :id")
  Optional<Task> findByIdWithAttachments(@Param("id") Long id);

  @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :now AND :sevenDaysLater")
  List<Task> findTasksDueWithinNextSevenDays(@Param("now") LocalDateTime now,
                                             @Param("sevenDaysLater") LocalDateTime sevenDaysLater);

  @Query(value = "SELECT * FROM tasks WHERE due_date BETWEEN NOW() AND NOW() + INTERVAL '7 days'",
          nativeQuery = true)
  List<Task> findTasksDueWithinNextSevenDaysNative();

  @Query("SELECT t FROM Task t WHERE t.completed = false AND t.dueDate < CURRENT_TIMESTAMP")
  List<Task> findOverdueTasks();

  @Query("SELECT t FROM Task t WHERE t.priority = :priority ORDER BY t.dueDate ASC")
  List<Task> findByPriorityOrderByDueDate(@Param("priority") Priority priority);

  @Modifying
  @Query("UPDATE Task t SET t.completed = true WHERE t.id IN :ids")
  int bulkCompleteTasks(@Param("ids") List<Long> ids);

  @Modifying
  @Query("DELETE FROM Task t WHERE t.id IN :ids")
  int bulkDeleteTasks(@Param("ids") List<Long> ids);

  @Modifying
  @Query("UPDATE Task t SET t.priority = :priority WHERE t.id IN :ids")
  int bulkUpdatePriority(@Param("ids") List<Long> ids, @Param("priority") Priority priority);
}