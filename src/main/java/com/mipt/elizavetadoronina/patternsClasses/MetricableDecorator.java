package com.mipt.elizavetadoronina.patternsClasses;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class MetricableDecorator implements DataService {
  private final DataService wrapped;
  private final MetricService metricService = new MetricService();

  public MetricableDecorator(DataService wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    Instant start = Instant.now();
    try {
      return wrapped.findDataByKey(key);
    } finally {
      metricService.sendMetric(Duration.between(start, Instant.now()));
    }
  }

  @Override
  public void saveData(String key, String data) {
    Instant start = Instant.now();
    try {
      wrapped.saveData(key, data);
    } finally {
      metricService.sendMetric(Duration.between(start, Instant.now()));
    }
  }

  @Override
  public boolean deleteData(String key) {
    Instant start = Instant.now();
    try {
      return wrapped.deleteData(key);
    } finally {
      metricService.sendMetric(Duration.between(start, Instant.now()));
    }
  }

  public static class MetricService {
    public void sendMetric(Duration duration) {
      System.out.println("Метод выполнялся: " + duration.toString());
    }
  }
}