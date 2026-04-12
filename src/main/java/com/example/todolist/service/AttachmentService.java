package com.example.todolist.service;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.dto.AttachmentUploadResponseDto;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.AttachmentMapper;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

  private final TaskAttachmentRepository attachmentRepository;
  private final TaskRepository taskRepository;
  private final AttachmentMapper attachmentMapper;

  @Value("${app.attachments.upload-dir:uploads}")
  private String uploadDirPath;

  private Path uploadDir;

  @PostConstruct
  public void init() throws IOException {
    this.uploadDir = Paths.get(uploadDirPath);
    Files.createDirectories(uploadDir);
    log.info("Upload directory initialized: {}", uploadDir.toAbsolutePath());
  }

  @Transactional(
          propagation = Propagation.REQUIRED,
          rollbackFor = {TaskNotFoundException.class, IOException.class}
  )
  public AttachmentUploadResponseDto storeAttachment(Long taskId, MultipartFile file) throws IOException {
    log.debug("Storing attachment for task ID: {}", taskId);

    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

    String originalFileName = file.getOriginalFilename();
    String extension = "";
    if (originalFileName != null && originalFileName.contains(".")) {
      extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    }
    String storedFileName = UUID.randomUUID().toString() + extension;

    Path targetLocation = uploadDir.resolve(storedFileName);
    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTask(task);
    attachment.setFileName(originalFileName);
    attachment.setStoredFileName(storedFileName);
    attachment.setContentType(file.getContentType());
    attachment.setSize(file.getSize());
    attachment.setUploadedAt(LocalDateTime.now());

    TaskAttachment saved = attachmentRepository.save(attachment);
    task.addAttachment(saved);
    taskRepository.save(task);

    String downloadUrl = "/api/attachments/" + saved.getId();

    return new AttachmentUploadResponseDto(
            "File uploaded successfully",
            attachmentMapper.toResponseDto(saved),
            downloadUrl
    );
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public TaskAttachment getAttachment(Long attachmentId) {
    return attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new RuntimeException("Attachment not found: " + attachmentId));
  }

  @Transactional(readOnly = true)
  public Resource loadAsResource(Long attachmentId) throws MalformedURLException {
    TaskAttachment attachment = getAttachment(attachmentId);
    Path filePath = uploadDir.resolve(attachment.getStoredFileName());
    Resource resource = new UrlResource(filePath.toUri());

    if (!resource.exists() || !resource.isReadable()) {
      throw new RuntimeException("File not found on disk: " + attachment.getStoredFileName());
    }

    return resource;
  }

  @Transactional(readOnly = true)
  public List<AttachmentResponseDto> getAttachmentsByTaskId(Long taskId) {
    log.debug("Getting attachments for task ID: {}", taskId);

    if (!taskRepository.existsById(taskId)) {
      throw new TaskNotFoundException(taskId);
    }

    List<TaskAttachment> attachments = attachmentRepository.findByTaskId(taskId);
    return attachmentMapper.toResponseDtoList(attachments);
  }

  @Transactional(rollbackFor = IOException.class)
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

    attachmentRepository.delete(attachment);

    log.debug("Attachment deleted successfully: {}, file deleted: {}", attachmentId, fileDeleted);
    return true;
  }

  public TaskAttachmentRepository getRepository() {
    return attachmentRepository;
  }
}