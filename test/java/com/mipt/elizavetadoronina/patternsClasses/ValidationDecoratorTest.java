package com.mipt.elizavetadoronina.patternsClasses;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ValidationDecoratorTest {

  static class TestDataService implements DataService {
    public String lastKey;
    public String lastData;

    @Override
    public Optional<String> findDataByKey(String key) {
      lastKey = key;
      return Optional.of("data");
    }

    @Override
    public void saveData(String key, String data) {
      lastKey = key;
      lastData = data;
    }

    @Override
    public boolean deleteData(String key) {
      lastKey = key;
      return true;
    }
  }

  @Test
  void shouldValidateKeyForFind() {
    TestDataService testService = new TestDataService();
    ValidationDecorator decorator = new ValidationDecorator(testService);

    assertThrows(IllegalArgumentException.class, () -> decorator.findDataByKey(null));
    assertThrows(IllegalArgumentException.class, () -> decorator.findDataByKey(""));
    assertThrows(IllegalArgumentException.class, () -> decorator.findDataByKey("   "));
  }

  @Test
  void shouldValidateKeyAndDataForSave() {
    TestDataService testService = new TestDataService();
    ValidationDecorator decorator = new ValidationDecorator(testService);

    assertThrows(IllegalArgumentException.class, () -> decorator.saveData(null, "data"));
    assertThrows(IllegalArgumentException.class, () -> decorator.saveData("", "data"));
    assertThrows(IllegalArgumentException.class, () -> decorator.saveData("test", null));
  }

  @Test
  void shouldValidateKeyForDelete() {
    TestDataService testService = new TestDataService();
    ValidationDecorator decorator = new ValidationDecorator(testService);

    assertThrows(IllegalArgumentException.class, () -> decorator.deleteData(null));
    assertThrows(IllegalArgumentException.class, () -> decorator.deleteData(""));
  }

  @Test
  void shouldPassValidDataToWrappedService() {
    TestDataService testService = new TestDataService();
    ValidationDecorator decorator = new ValidationDecorator(testService);

    decorator.findDataByKey("valid");
    assertEquals("valid", testService.lastKey);

    decorator.saveData("valid", "data");
    assertEquals("valid", testService.lastKey);
    assertEquals("data", testService.lastData);

    decorator.deleteData("valid");
    assertEquals("valid", testService.lastKey);
  }
}