package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
  @Schema(description = "ID задачи", example = "1")
  private Long id;

  @Schema(description = "Название задачи", example = "Купить продукты")
  private String title;

  @Schema(description = "Описание задачи", example = "Молоко, хлеб, яйца")
  private String description;

  @Schema(description = "Статус выполнения", example = "false")
  private boolean completed;

  @Schema(description = "Дата и время создания задачи")
  private LocalDateTime createdAt;

  @Schema(description = "Срок выполнения задачи")
  private LocalDate dueDate;

  @Schema(description = "Приоритет задачи")
  private Priority priority;

  @Schema(description = "Список тегов")
  private Set<String> tags;
}