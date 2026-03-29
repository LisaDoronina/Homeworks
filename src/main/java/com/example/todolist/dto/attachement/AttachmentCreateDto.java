package com.example.todolist.dto.attachement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AttachmentCreateDto {

  @NotNull
  Long taskId;

  @NotBlank
  String fileName;

  @NotBlank
  String contentType;

  @NotBlank
  Long size;
}