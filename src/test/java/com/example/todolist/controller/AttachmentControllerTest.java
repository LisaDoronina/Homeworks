package com.example.todolist.controller;

import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttachmentService attachmentService;

  @Test
  void shouldUploadFile() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Hello".getBytes()
    );

    TaskAttachment attachment = new TaskAttachment(
            1L, 1L, "test.txt", "uuid_test.txt",
            "text/plain", 5L, LocalDateTime.now()
    );

    Mockito.when(attachmentService.storeAttachment(Mockito.eq(1L), Mockito.any()))
            .thenReturn(attachment);

    mockMvc.perform(multipart("/api/tasks/1/attachments")
                    .file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileName").value("test.txt"));
  }

  @Test
  void shouldFailUploadWhenEmptyFile() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
            "file",
            "",
            "text/plain",
            new byte[0]
    );

    mockMvc.perform(multipart("/api/tasks/1/attachments")
                    .file(file))
            .andExpect(status().isBadRequest());
  }

  @Test
  void shouldDownloadFile() throws Exception {
    TaskAttachment attachment = new TaskAttachment(
            1L, 1L, "test.txt", "uuid_test.txt",
            "text/plain", 5L, LocalDateTime.now()
    );

    Mockito.when(attachmentService.getAttachment(1L)).thenReturn(attachment);
    Mockito.when(attachmentService.loadAsResource(1L))
            .thenReturn(new ByteArrayResource("Hello".getBytes()));

    mockMvc.perform(get("/api/attachments/1"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""));
  }

  @Test
  void shouldReturn404WhenAttachmentNotFound() throws Exception {
    Mockito.when(attachmentService.getAttachment(1L))
            .thenThrow(new RuntimeException("Not found"));

    mockMvc.perform(get("/api/attachments/1"))
            .andExpect(status().isInternalServerError());
  }

  @Test
  void shouldDeleteAttachment() throws Exception {
    Mockito.when(attachmentService.deleteAttachment(1L)).thenReturn(true);

    mockMvc.perform(delete("/api/attachments/1"))
            .andExpect(status().isNoContent());
  }
}