package com.mipt.elizavetadoronina.patternsClasses;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MetricableDecoratorTest {

  static class TestDataService implements DataService {
    @Override
    public Optional<String> findDataByKey(String key) {
      return Optional.of("data");
    }

    @Override
    public void saveData(String key, String data) {
      // имитация работы
    }

    @Override
    public boolean deleteData(String key) {
      return true;
    }
  }

  @Test
  void shouldSendMetricsForFind() {
    TestDataService testService = new TestDataService();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    MetricableDecorator decorator = new MetricableDecorator(testService);
    decorator.findDataByKey("test");

    assertTrue(out.toString().contains("Метод выполнялся: PT"));
  }

  @Test
  void shouldSendMetricsForSave() {
    TestDataService testService = new TestDataService();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    MetricableDecorator decorator = new MetricableDecorator(testService);
    decorator.saveData("test", "data");

    assertTrue(out.toString().contains("Метод выполнялся: PT"));
  }

  @Test
  void shouldSendMetricsForDelete() {
    TestDataService testService = new TestDataService();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    MetricableDecorator decorator = new MetricableDecorator(testService);
    decorator.deleteData("test");

    assertTrue(out.toString().contains("Метод выполнялся: PT"));
  }
}