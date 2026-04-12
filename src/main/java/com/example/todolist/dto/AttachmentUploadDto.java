package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на загрузку файла")
public class AttachmentUploadDto {

  @NotNull(message = "ID задачи обязателен")
  @Positive(message = "ID задачи должен быть положительным числом")
  @Schema(description = "ID задачи", example = "10", required = true)
  private Long taskId;

  @Schema(description = "Оригинальное имя файла", example = "document.pdf")
  private String fileName;

  @Schema(description = "Тип содержимого", example = "application/pdf")
  private String contentType;

  @Schema(description = "Размер файла в байтах", example = "1048576")
  private Long size;
}