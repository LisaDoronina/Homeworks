package com.example.todolist.dto;

import jakarta.validation.constraints.NotBlank;

public record ExternalTaskCreateDto(
        @NotBlank String title,
        String description,
        boolean completed
) {}
