package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
public class AttachmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttachmentService attachmentService;

  @Test
  void testUploadAttachment_success() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello".getBytes());

    TaskAttachment attachment = new TaskAttachment();
    attachment.setId(1L);
    attachment.setFileName("test.txt");
    attachment.setSize(file.getSize());
    attachment.setUploadedAt(LocalDateTime.now());

    Mockito.when(attachmentService.storeAttachment(eq(1L), any())).thenReturn(attachment);

    mockMvc.perform(multipart("/api/tasks/1/attachments")
                    .file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void testGetAttachments_success() throws Exception {
    TaskAttachment attachment = new TaskAttachment(1L, 1L, "test.txt", "uuid.txt",
            "text/plain", 5L, LocalDateTime.now());
    AttachmentResponseDto dto = new AttachmentResponseDto(1L, "test.txt", 5L, LocalDateTime.now());

    Mockito.when(attachmentService.getAttachment(1L))
            .thenReturn(attachment);

    mockMvc.perform(get("/api/tasks/1/attachments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fileName").value("test.txt"));
  }
}