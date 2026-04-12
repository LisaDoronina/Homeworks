package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о загруженном файле")
public class AttachmentResponseDto {

  @Schema(description = "ID вложения", example = "1")
  private Long id;

  @Schema(description = "ID задачи", example = "10")
  private Long taskId;

  @Schema(description = "Оригинальное имя файла", example = "report.docx")
  private String fileName;

  @Schema(description = "Тип содержимого", example = "application/pdf")
  private String contentType;

  @Schema(description = "Размер в байтах", example = "102400")
  private Long size;

  @Schema(description = "Дата и время загрузки")
  private LocalDateTime uploadedAt;
}