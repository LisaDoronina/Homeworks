package com.example.todolist.model;

import com.example.todolist.validation.DueDateNotBeforeCreation;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@DueDateNotBeforeCreation
public class Task {

  private Long id;

  private String title;

  private String description;

  private boolean completed;

  private LocalDateTime createdAt;

  private LocalDateTime dueDate;

  private Priority priority;

  private Set<String> tags;

  public Task(Long id, String title, String description, Boolean completed) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.completed = completed;
  }

  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}