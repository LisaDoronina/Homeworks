package com.mipt.elizavetadoronina.patternsClasses;

import java.util.Optional;

public class ValidationDecorator implements DataService {
  private final DataService wrapped;

  public ValidationDecorator(DataService wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("Ключ не может быть пустым");
    }
    return wrapped.findDataByKey(key);
  }

  @Override
  public void saveData(String key, String data) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("Ключ не может быть пустым");
    }
    if (data == null) {
      throw new IllegalArgumentException("Данные не могут быть null");
    }
    wrapped.saveData(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    if (key == null || key.trim().isEmpty()) {
      throw new IllegalArgumentException("Ключ не может быть пустым");
    }
    return wrapped.deleteData(key);
  }
}