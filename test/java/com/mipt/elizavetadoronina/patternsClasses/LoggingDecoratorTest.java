package com.mipt.elizavetadoronina.patternsClasses;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LoggingDecoratorTest {

  static class TestDataService implements DataService {
    @Override
    public Optional<String> findDataByKey(String key) {
      return Optional.of("data");
    }

    @Override
    public void saveData(String key, String data) {
    }

    @Override
    public boolean deleteData(String key) {
      return true;
    }
  }

  @Test
  void shouldLogFindOperation() {
    TestDataService testService = new TestDataService();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    LoggingDecorator decorator = new LoggingDecorator(testService);
    decorator.findDataByKey("test");

    assertTrue(out.toString().contains("Поиск данных по ключу: test"));
  }

  @Test
  void shouldLogSaveOperation() {
    TestDataService testService = new TestDataService();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    LoggingDecorator decorator = new LoggingDecorator(testService);
    decorator.saveData("test", "data");

    assertTrue(out.toString().contains("Сохранение данных с ключом: test"));
  }

  @Test
  void shouldLogDeleteOperation() {
    TestDataService testService = new TestDataService();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    LoggingDecorator decorator = new LoggingDecorator(testService);
    decorator.deleteData("test");

    assertTrue(out.toString().contains("Удаление данных с ключом: test"));
  }
}