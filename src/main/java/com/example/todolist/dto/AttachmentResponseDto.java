package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Информация о загруженном файле")
public class AttachmentResponseDto {

  @Schema(description = "ID", example = "1")
  private Long id;

  @Schema(description = "Имя", example = "file.txt")
  private String fileName;

  @Schema(description = "Размер в байтах", example = "102400")
  private long size;

  @Schema(description = "Дата и время загрузки")
  private LocalDateTime uploadedAt;
}