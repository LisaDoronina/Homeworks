package com.example.todolist.exception;

import java.util.List;

public class BulkOperationException extends RuntimeException {

  private final List<Long> missingIds;

  public BulkOperationException(String message, List<Long> missingIds) {
    super(message);
    this.missingIds = missingIds;
  }

  public List<Long> getMissingIds() {
    return missingIds;
  }
}