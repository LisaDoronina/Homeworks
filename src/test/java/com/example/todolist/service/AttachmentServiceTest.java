package com.example.todolist.service;

import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentServiceTest {

  @Test
  void shouldStoreFile() throws IOException {
    TaskAttachmentRepository repo = Mockito.mock(TaskAttachmentRepository.class);
    AttachmentService service = new AttachmentService(repo);

    MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Hello".getBytes()
    );

    Mockito.when(repo.save(Mockito.any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    TaskAttachment result = service.storeAttachment(1L, file);

    assertEquals("test.txt", result.getFileName());
    assertEquals(1L, result.getTaskId());
  }

  @Test
  void shouldDeleteAttachment() throws IOException {
    TaskAttachmentRepository repo = Mockito.mock(TaskAttachmentRepository.class);
    AttachmentService service = new AttachmentService(repo);

    TaskAttachment attachment = new TaskAttachment(
            1L, 1L, "file.txt", "uuid.txt",
            "text/plain", 10, LocalDateTime.now()
    );

    Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(attachment));
    Mockito.when(repo.delete(1L)).thenReturn(true);

    boolean result = service.deleteAttachment(1L);

    assertTrue(result);
  }
}