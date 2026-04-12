package com.example.todolist.service;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

  @InjectMocks
  private AttachmentService attachmentService;  // Прямо сервис, без Impl

  private Task task;
  private TaskAttachment attachment;
  private MultipartFile multipartFile;

  @BeforeEach
  void setUp() {
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

    multipartFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
    );
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
  void deleteAttachment_ShouldReturnTrue() throws Exception {
    when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
    doNothing().when(attachmentRepository).delete(attachment.getId());

    boolean result = attachmentService.deleteAttachment(1L);

    assertThat(result).isTrue();
    verify(attachmentRepository).delete(attachment.getId());
  }

  @Test
  void deleteAttachment_WhenNotFound_ShouldReturnFalse() throws Exception {
    when(attachmentRepository.findById(1L)).thenReturn(Optional.empty());

    boolean result = attachmentService.deleteAttachment(1L);

    assertThat(result).isFalse();
    verify(attachmentRepository, never()).delete(any());
  }

  @Test
  void getRepository_ShouldReturnRepository() {
    TaskAttachmentRepository repository = attachmentService.getRepository();
    assertThat(repository).isEqualTo(attachmentRepository);
  }
}