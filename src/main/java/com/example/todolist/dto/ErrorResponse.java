package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация об ошибке API")
public class ErrorResponse {

  @Schema(description = "Время возникновения ошибки", example = "2026-04-55T12:45:30Z")
  private Instant timestamp;

  @Schema(description = "HTTP статус ошибки", example = "400")
  private int status;

  @Schema(description = "Краткое описание ошибки", example = "Bad Request")
  private String error;

  @Schema(description = "Подробное сообщение для клиента", example = "Поле title не может быть пустым")
  private String message;

  @Schema(description = "Путь запроса, вызвавшего ошибку", example = "/api/tasks")
  private String path;

  @Schema(description = "Some details here")
  private Map<String, Object> details;
}