package com.example.todolist.service;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.dto.AttachmentUploadResponseDto;
import com.example.todolist.mapper.AttachmentMapper;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttachmentService {

  private final TaskAttachmentRepository attachmentRepository;
  private final TaskRepository taskRepository;
  private final AttachmentMapper attachmentMapper;

  @Value("${app.attachments.upload-dir:uploads}")
  private String uploadDirPath;

  private Path uploadDir;

  @jakarta.annotation.PostConstruct
  public void init() throws IOException {
    this.uploadDir = Paths.get(uploadDirPath);
    Files.createDirectories(uploadDir);
    log.info("Upload directory initialized: {}", uploadDir.toAbsolutePath());
  }

  public AttachmentUploadResponseDto storeAttachment(Long taskId, MultipartFile file) throws IOException {
    log.debug("Storing attachment for task ID: {}", taskId);

    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

    String originalFileName = file.getOriginalFilename();
    String extension = "";
    if (originalFileName != null && originalFileName.contains(".")) {
      extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    }
    String storedFileName = UUID.randomUUID().toString() + extension;

    Path targetLocation = uploadDir.resolve(storedFileName);
    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTask(task);
    attachment.setFileName(originalFileName);
    attachment.setStoredFileName(storedFileName);
    attachment.setContentType(file.getContentType());
    attachment.setSize(file.getSize());
    attachment.setUploadedAt(LocalDateTime.now());

    TaskAttachment savedAttachment = attachmentRepository.save(attachment);

    task.addAttachment(savedAttachment);
    taskRepository.save(task);

    String downloadUrl = "/api/attachments/" + savedAttachment.getId();

    return new AttachmentUploadResponseDto(
            "File uploaded successfully",
            attachmentMapper.toResponseDto(savedAttachment),
            downloadUrl
    );
  }

  @Transactional(readOnly = true)
  public TaskAttachment getAttachment(Long attachmentId) {
    return attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + attachmentId));
  }

  @Transactional(readOnly = true)
  public Resource loadAsResource(Long attachmentId) throws MalformedURLException {
    TaskAttachment attachment = getAttachment(attachmentId);
    Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();
    Resource resource = new UrlResource(filePath.toUri());

    if (!resource.exists() || !resource.isReadable()) {
      throw new RuntimeException("File not found on disk: " + attachment.getStoredFileName());
    }

    return resource;
  }

  @Transactional(readOnly = true)
  public List<AttachmentResponseDto> getAttachmentsByTaskId(Long taskId) {
    log.debug("Getting attachments for task ID: {}", taskId);

    List<TaskAttachment> attachments = attachmentRepository.findByTaskId(taskId);
    return attachmentMapper.toResponseDtoList(attachments);
  }

  public boolean deleteAttachment(Long attachmentId) throws IOException {
    log.debug("Deleting attachment with ID: {}", attachmentId);

    TaskAttachment attachment = attachmentRepository.findById(attachmentId)
            .orElse(null);

    if (attachment == null) {
      log.warn("Attachment not found with ID: {}", attachmentId);
      return false;
    }

    Path filePath = uploadDir.resolve(attachment.getStoredFileName());
    boolean fileDeleted = Files.deleteIfExists(filePath);

    if (!fileDeleted) {
      log.warn("File not found on disk: {}", attachment.getStoredFileName());
    }

    attachmentRepository.delete(attachment.getId());

    log.debug("Attachment deleted successfully: {}, file deleted: {}", attachmentId, fileDeleted);
    return true;
  }

  public TaskAttachmentRepository getRepository() {
    return attachmentRepository;
  }
}