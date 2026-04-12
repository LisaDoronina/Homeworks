package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import com.example.todolist.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания новой задачи")
public class TaskCreateDto {

  @NotBlank(groups = OnCreate.class)
  @Size(min = 5, max = 100, groups = OnCreate.class)
  @Schema(description = "Название задачи", example = "Послушать Radiohead", minLength = 5, maxLength = 100, required = true)
  private String title;

  @Size(max = 300, groups = OnCreate.class)
  @Schema(description = "Описание задачи", example = "Долго выбирать альбом, который еще не прослушан. В конце концов просто снова включить Kid A.", maxLength = 300)
  private String description;

  @FutureOrPresent(groups = OnCreate.class)
  @Schema(description = "Срок выполнения задачи", example = "2027-03-23", required = true)
  private LocalDateTime dueDate;

  @NotNull(groups = OnCreate.class)
  @Schema(description = "Приоритет задачи", example = "HIGH", required = true)
  private Priority priority;

  @Size(max = 5, groups = OnCreate.class)
  @Schema(description = "Набор тегов для задачи", example = "[\"Music\", \"Radiohead\"]")
  private Set<String> tags;


  public TaskCreateDto(String title, String description, LocalDateTime dueDate, Priority priority, String tags) {
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
    this.priority = priority != null ? priority : Priority.MEDIUM;
    if (tags != null && !tags.isEmpty()) {
      this.tags = Set.of(tags.split(","));
    }
  }
}