package com.example.todolist.dto.attachement;

import jakarta.validation.constraints.NotBlank;

public class AttachmentUpdateDto {

  @NotBlank
  String fileName;

  @NotBlank
  String contentType;
}