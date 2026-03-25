package com.example.todolist.validation;

import com.example.todolist.model.Task;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;

public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, Object> {

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
    LocalDate dueDate = (LocalDate) wrapper.getPropertyValue("dueDate");

    if (dueDate == null) {
      return true;
    }

    Task existingTask = (Task) wrapper.getPropertyValue("existingTask");

    if (existingTask == null) {
      return true;
    }

    LocalDate creationDate = existingTask.getCreatedAt().toLocalDate();
    return !dueDate.isBefore(creationDate);
  }
}