package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.dto.AttachmentUploadResponseDto;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AttachmentControllerTest {

  @Mock
  private AttachmentService attachmentService;

  @InjectMocks
  private AttachmentController attachmentController;

  private MockMvc mockMvc;
  private AttachmentUploadResponseDto uploadResponse;
  private AttachmentResponseDto attachmentResponse;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).build();

    attachmentResponse = new AttachmentResponseDto();
    attachmentResponse.setId(1L);
    attachmentResponse.setTaskId(1L);
    attachmentResponse.setFileName("test.txt");
    attachmentResponse.setContentType("text/plain");
    attachmentResponse.setSize(1024L);
    attachmentResponse.setUploadedAt(LocalDateTime.now());

    uploadResponse = new AttachmentUploadResponseDto(
            "File uploaded successfully",
            attachmentResponse,
            "/api/attachments/1/download"
    );
  }

  @Test
  void uploadFile_ShouldReturnCreated() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "test content".getBytes()
    );

    when(attachmentService.storeAttachment(eq(1L), any()))
            .thenReturn(uploadResponse);

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 1L)
                    .file(file))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("File uploaded successfully"))
            .andExpect(jsonPath("$.attachment.fileName").value("test.txt"));
  }

  @Test
  void getAttachmentsForTask_ShouldReturnList() throws Exception {
    when(attachmentService.getAttachmentsByTaskId(1L))
            .thenReturn(java.util.List.of(attachmentResponse));

    mockMvc.perform(get("/api/tasks/{taskId}/attachments", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fileName").value("test.txt"));
  }

  @Test
  void deleteAttachment_ShouldReturnNoContent() throws Exception {
    when(attachmentService.deleteAttachment(1L)).thenReturn(true);

    mockMvc.perform(delete("/api/attachments/{attachmentId}", 1L))
            .andExpect(status().isNoContent());
  }
}