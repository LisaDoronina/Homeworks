package com.example.todolist.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "task")
public class TaskAttachment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @Column(name = "stored_file_name", nullable = false, length = 255)
  private String storedFileName;

  @Column(name = "content_type", nullable = false, length = 255)
  private String contentType;

  @Column(nullable = false)
  private Long size;

  @CreatedDate
  @Column(name = "uploaded_at", nullable = false, updatable = false)
  private LocalDateTime uploadedAt;


  public TaskAttachment(Task task, String fileName, String storedFileName, String contentType, Long size) {
    this.task = task;
    this.fileName = fileName;
    this.storedFileName = storedFileName;
    this.contentType = contentType;
    this.size = size;
  }
}