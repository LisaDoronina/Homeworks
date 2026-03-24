package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AttachmentController {

  private final AttachmentService attachmentService;

  public AttachmentController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @PostMapping("/tasks/{taskId}/attachments")
  public AttachmentResponseDto uploadFile(
          @PathVariable Long taskId,
          @RequestParam("file") MultipartFile file) throws IOException {

    TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
    return new AttachmentResponseDto(
            attachment.getId(),
            attachment.getFileName(),
            attachment.getSize(),
            attachment.getUploadedAt()
    );
  }

  @GetMapping("/attachments/{attachmentId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) throws IOException {
    TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
    Resource resource = attachmentService.loadAsResource(attachmentId);

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(attachment.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
            .body(resource);
  }

  @DeleteMapping("/attachments/{attachmentId}")
  public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) throws IOException {
    if (attachmentService.deleteAttachment(attachmentId)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/tasks/{taskId}/attachments")
  public List<AttachmentResponseDto> getAttachmentsForTask(@PathVariable Long taskId) {
    return attachmentService.repository.findByTaskId(taskId)
            .stream()
            .map(a -> new AttachmentResponseDto(a.getId(), a.getFileName(), a.getSize(), a.getUploadedAt()))
            .collect(Collectors.toList());
  }
}