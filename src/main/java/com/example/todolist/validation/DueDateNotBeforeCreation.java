package com.example.todolist.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Documented
public @interface DueDateNotBeforeCreation {
  String message() default "Due date cannot be before task creation date";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}