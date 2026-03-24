package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import com.example.todolist.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для обновления задачи")
public class TaskUpdateDto {

  @Size(min = 5, max = 100, groups = OnUpdate.class)
  @Schema(description = "Название задачи", example = "Послушать Rammstein", minLength = 5, maxLength = 100)
  private String title;

  @Size(max = 300, groups = OnUpdate.class)
  @Schema(description = "Описание задачи", example = "Найти невыложенный Zeit на какой-нибудь платформе и послушать первую и последнюю песню.", maxLength = 300)
  private String description;

  @Schema(description = "Флаг завершенности задачи", example = "true")
  private boolean completed;

  @Schema(description = "Срок выполнения задачи", example = "2027-03-23")
  private LocalDate dueDate;

  @Schema(description = "Приоритет задачи", example = "MEDIUM")
  private Priority priority;

  @Size(max = 5, groups = OnUpdate.class)
  @Schema(description = "Набор тегов", example = "[\"Music\", \"Rammstein\"]")
  private Set<String> tags;
}