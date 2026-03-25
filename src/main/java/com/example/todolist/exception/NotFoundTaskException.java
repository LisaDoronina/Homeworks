package com.example.todolist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotFoundTaskException extends RuntimeException {
  public NotFoundTaskException(Long id) {
    super("Task not found with id: " + id);
  }
}
