package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.dto.AttachmentUploadResponseDto;
import com.example.todolist.mapper.AttachmentMapper;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "API для работы с вложениями задач")
public class AttachmentController {

  private final AttachmentService attachmentService;
  private final AttachmentMapper attachmentMapper;

  @PostMapping("/tasks/{taskId}/attachments")
  @Operation(summary = "Загрузить файл для задачи")
  public ResponseEntity<AttachmentUploadResponseDto> uploadFile(
          @Parameter(description = "ID задачи", example = "1")
          @PathVariable Long taskId,
          @Parameter(description = "Файл для загрузки")
          @RequestParam("file") MultipartFile file) throws IOException {

    AttachmentUploadResponseDto response = attachmentService.storeAttachment(taskId, file);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/attachments/{attachmentId}")
  @Operation(summary = "Скачать файл по ID вложения")
  public ResponseEntity<Resource> downloadFile(
          @Parameter(description = "ID вложения", example = "1")
          @PathVariable Long attachmentId) throws MalformedURLException {

    TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
    Resource resource = attachmentService.loadAsResource(attachmentId);

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(attachment.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + attachment.getFileName() + "\"")
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(attachment.getSize()))
            .body(resource);
  }

  @DeleteMapping("/attachments/{attachmentId}")
  @Operation(summary = "Удалить вложение")
  public ResponseEntity<Void> deleteAttachment(
          @Parameter(description = "ID вложения", example = "1")
          @PathVariable Long attachmentId) throws IOException {

    if (attachmentService.deleteAttachment(attachmentId)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/tasks/{taskId}/attachments")
  @Operation(summary = "Получить все вложения задачи")
  public ResponseEntity<List<AttachmentResponseDto>> getAttachmentsForTask(
          @Parameter(description = "ID задачи", example = "1")
          @PathVariable Long taskId) {

    List<AttachmentResponseDto> attachments = attachmentService.getAttachmentsByTaskId(taskId);
    return ResponseEntity.ok(attachments);
  }
}