package com.example.todolist.service;

import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttachmentService {

  private final Path uploadDir = Paths.get("uploads");
  public final TaskAttachmentRepository repository;

  public AttachmentService(TaskAttachmentRepository repository) throws IOException {
    this.repository = repository;
    Files.createDirectories(uploadDir);
  }

  public TaskAttachment storeAttachment(Long taskId, MultipartFile file) throws IOException {
    String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path targetLocation = uploadDir.resolve(storedFileName);

    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    TaskAttachment attachment = new TaskAttachment(
            null,
            taskId,
            file.getOriginalFilename(),
            storedFileName,
            file.getContentType(),
            file.getSize(),
            LocalDateTime.now()
    );

    return repository.save(attachment);
  }

  public TaskAttachment getAttachment(Long attachmentId) {
    return repository.findById(attachmentId)
            .orElseThrow(() -> new RuntimeException("Attachment not found"));
  }

  public Resource loadAsResource(Long attachmentId) throws MalformedURLException {
    TaskAttachment attachment = getAttachment(attachmentId);
    Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();
    Resource resource = new UrlResource(filePath.toUri());
    if (!resource.exists()) throw new RuntimeException("File not found on disk");
    return resource;
  }

  public boolean deleteAttachment(Long attachmentId) throws IOException {
    TaskAttachment attachment = getAttachment(attachmentId);
    Path filePath = uploadDir.resolve(attachment.getStoredFileName());
    Files.deleteIfExists(filePath);
    return repository.delete(attachmentId);
  }
}