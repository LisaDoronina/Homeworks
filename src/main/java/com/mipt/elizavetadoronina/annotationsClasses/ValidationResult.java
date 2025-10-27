package com.mipt.elizavetadoronina.annotationsClasses;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
  private boolean valid;
  private final List<String> errors = new ArrayList<>();

  public ValidationResult() {
    this.valid = true;
  }

  public boolean isValid() {
    return valid;
  }

  public List<String> getErrors() {
    return new ArrayList<>(errors);
  }

  public void addError(String error) {
    valid = false;
    errors.add(error);
  }
}