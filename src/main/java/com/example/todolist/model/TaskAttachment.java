package com.example.todolist.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachment {

  private Long id;

  private Long taskId;

  private String fileName;

  private String storedFileName;

  private String contentType;

  private long size;

  private LocalDateTime uploadedAt;

  protected void onCreate() {
    uploadedAt = LocalDateTime.now();
  }
}