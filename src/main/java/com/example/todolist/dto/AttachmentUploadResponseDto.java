package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ после загрузки файла")
public class AttachmentUploadResponseDto {

  @Schema(description = "Сообщение об успешной загрузке", example = "File uploaded successfully")
  private String message;

  @Schema(description = "Информация о загруженном файле")
  private AttachmentResponseDto attachment;

  @Schema(description = "URL для скачивания", example = "/api/tasks/10/attachments/1/download")
  private String downloadUrl;
}