package com.example.todolist.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "attachments")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private boolean completed;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priority;

  @Column(columnDefinition = "TEXT")
  private String tags;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<TaskAttachment> attachments = new ArrayList<>();

  public Task(String title, String description, boolean completed, LocalDateTime dueDate, Priority priority, String tags) {
    this.title = title;
    this.description = description;
    this.completed = completed;
    this.dueDate = dueDate;
    this.priority = priority;
    setTagSet(tags);
  }

  public Task(String title, String description) {
    this.title = title;
    this.description = description;
    this.completed = false;
    this.priority = Priority.MEDIUM;
  }

  public void addAttachment(TaskAttachment attachment) {
    attachments.add(attachment);
    attachment.setTask(this);
  }

  public void removeAttachment(TaskAttachment attachment) {
    attachments.remove(attachment);
    attachment.setTask(null);
  }

  public void removeAllAttachments() {
    attachments.clear();
  }

  public Set<String> getTagSet() {
    if (tags == null || tags.isEmpty()) {
      return new HashSet<>();
    }
    String[] parts = tags.split(",");
    Set<String> result = new HashSet<>();
    for (String part : parts) {
      result.add(part.trim());
    }
    return result;
  }

  public void setTagSet(String tagSet) {
    if (tagSet == null || tagSet.isEmpty()) {
      this.tags = null;
    } else {
      this.tags = String.join(",", tagSet);
    }
  }


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
    return "Task{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", completed=" + completed +
            ", priority=" + priority +
            ", dueDate=" + dueDate +
            '}';
  }
}