package com.example.todolist.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DueDateNotBeforeCreation {

  String message() default "dueDate не может быть раньше createdAt";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}