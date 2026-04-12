package com.example.todolist.exception;

import com.example.todolist.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @Value("${api.version:2.0.0}")
  private String apiVersion;

  private ResponseEntity<Object> buildResponse(HttpStatus status, String error, String message, WebRequest request, Map<String, Object> details) {
    ErrorResponse body = new ErrorResponse();
    body.setTimestamp(Instant.now());
    body.setStatus(status.value());
    body.setError(error);
    body.setMessage(message);
    body.setPath(request.getDescription(false).replace("uri=", ""));
    body.setDetails(details);

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-Version", apiVersion);

    return new ResponseEntity<>(body, headers, status);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, Object> fieldErrors = new HashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Validation failed", request, fieldErrors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
    Map<String, Object> violations = new HashMap<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      violations.put(violation.getPropertyPath().toString(), violation.getMessage());
    }
    return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Constraint violation", request, violations);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Object> handleMissingParams(MissingServletRequestParameterException ex, WebRequest request) {
    Map<String, Object> details = Map.of(ex.getParameterName(), "Parameter is missing");
    return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request, details);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Malformed JSON request", request, null);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<Object> handleNoHandler(NoHandlerFoundException ex, WebRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, "Not Found", "Endpoint not found", request, null);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<Object> handleTaskNotFound(TaskNotFoundException ex, WebRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request, null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
    Map<String, Object> details = new HashMap<>();
    details.put("exception", ex.getClass().getSimpleName());
    if ("dev".equals(System.getProperty("spring.profiles.active"))) {
      details.put("stackTrace", ex.getStackTrace());
    }
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request, details);
  }
}