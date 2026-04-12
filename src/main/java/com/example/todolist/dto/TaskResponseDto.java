package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с информацией о задаче")
public class TaskResponseDto {

  @Schema(description = "ID задачи", example = "1")
  private Long id;

  @Schema(description = "Название задачи", example = "Подготовить отчёт")
  private String title;

  @Schema(description = "Описание задачи", example = "Сделать финальную версию отчёта")
  private String description;

  @Schema(description = "Статус выполнения", example = "false")
  private boolean completed;

  @Schema(description = "Дата создания")
  private LocalDateTime createdAt;

  @Schema(description = "Дата обновления")
  private LocalDateTime updatedAt;

  @Schema(description = "Дедлайн", example = "2024-12-31")
  private LocalDateTime dueDate;

  @Schema(description = "Приоритет", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
  private String priority;

  @Schema(description = "Теги", example = "[\"work\", \"urgent\"]")
  private Set<String> tags;

  @Schema(description = "Список вложений")
  private List<AttachmentResponseDto> attachments;

  public TaskResponseDto(Long id, String title, String description, boolean completed,
                         LocalDateTime createdAt, LocalDateTime dueDate, Priority priority, String tags) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.completed = completed;
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
    this.dueDate = dueDate;
    this.priority = priority != null ? priority.name() : Priority.MEDIUM.name();
    if (tags != null && !tags.isEmpty()) {
      this.tags = Set.of(tags.split(","));
    }
    this.attachments = List.of();
  }
}