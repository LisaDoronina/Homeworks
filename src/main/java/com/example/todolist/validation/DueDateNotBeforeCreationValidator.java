package com.example.todolist.validation;

import com.example.todolist.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

  @Override
  public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
    if (dto.getDueDate() == null) {
      return true;
    }

    return true;
  }
}