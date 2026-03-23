package com.example.todolist.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Task {
  private Long id;
  private String title;
  private String description;
  private boolean completed;

  private LocalDateTime createdAt;
  private LocalDate dueDate;
  private Priority priority;
  private Set<String> tags;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return completed == task.completed &&
            Objects.equals(id, task.id) &&
            Objects.equals(title, task.title) &&
            Objects.equals(description, task.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, completed);
  }

  @Override
  public String toString() {
    return "Task {" + "id = " + id +
            ", title = " + title +
            ", description = " + description +
            ", completed = " + completed + "}";
  }
}