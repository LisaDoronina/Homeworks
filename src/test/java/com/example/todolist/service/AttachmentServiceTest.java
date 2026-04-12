package com.example.todolist.service;

import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.AttachmentMapper;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

  @Mock
  private TaskAttachmentRepository attachmentRepository;

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private AttachmentMapper attachmentMapper;

  @Mock
  private MultipartFile multipartFile;

  @InjectMocks
  private AttachmentService attachmentService;

  private Task task;
  private TaskAttachment attachment;

  @BeforeEach
  void setUp() throws IOException {
    Path uploadPath = Paths.get("target/test-uploads");
    ReflectionTestUtils.setField(attachmentService, "uploadDirPath", "target/test-uploads");
    ReflectionTestUtils.setField(attachmentService, "uploadDir", uploadPath);

    attachmentService.init();

    task = new Task();
    task.setId(1L);
    task.setTitle("Test Task");

    attachment = new TaskAttachment();
    attachment.setId(1L);
    attachment.setTask(task);
    attachment.setFileName("test.txt");
    attachment.setStoredFileName("uuid-test.txt");
    attachment.setContentType("text/plain");
    attachment.setSize(1024L);
    attachment.setUploadedAt(LocalDateTime.now());
  }

  @Test
  void getAttachment_ShouldReturnAttachment() {
    when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));

    TaskAttachment result = attachmentService.getAttachment(1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getFileName()).isEqualTo("test.txt");
  }

  @Test
  void getAttachment_WhenNotFound_ShouldThrowException() {
    when(attachmentRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> attachmentService.getAttachment(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Attachment not found");
  }

  @Test
  void deleteAttachment_ShouldReturnTrue() throws Exception {
    when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
    doNothing().when(attachmentRepository).delete(attachment);

    boolean result = attachmentService.deleteAttachment(1L);

    assertThat(result).isTrue();
    verify(attachmentRepository).delete(attachment);
  }

  @Test
  void deleteAttachment_WhenNotFound_ShouldReturnFalse() throws Exception {
    when(attachmentRepository.findById(999L)).thenReturn(Optional.empty());

    boolean result = attachmentService.deleteAttachment(999L);

    assertThat(result).isFalse();
    verify(attachmentRepository, never()).delete(any());
  }
}