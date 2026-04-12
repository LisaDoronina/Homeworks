package com.example.todolist.repository;

import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

  List<TaskAttachment> findByTask(Task task);

  List<TaskAttachment> findByTaskId(Long taskId);

  List<TaskAttachment> findByContentTypeContaining(String contentType);

  long countByTaskId(Long taskId);

  @Modifying
  @Transactional
  @Query("DELETE FROM TaskAttachment ta WHERE ta.task.id = :taskId")
  void deleteByTaskId(@Param("taskId") Long taskId);

  List<TaskAttachment> findBySizeGreaterThan(Long size);
}