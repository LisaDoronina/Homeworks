package com.mipt.elizavetadoronina.patternsClasses;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class CachingDecoratorTest {

  static class TestDataService implements DataService {
    public int findCallCount = 0;
    public int saveCallCount = 0;
    public int deleteCallCount = 0;

    @Override
    public Optional<String> findDataByKey(String key) {
      findCallCount++;
      return Optional.of("data-from-service");
    }

    @Override
    public void saveData(String key, String data) {
      saveCallCount++;
    }

    @Override
    public boolean deleteData(String key) {
      deleteCallCount++;
      return true;
    }
  }

  @Test
  void shouldCacheFindResults() {
    TestDataService testService = new TestDataService();
    CachingDecorator decorator = new CachingDecorator(testService);

    decorator.findDataByKey("test");
    decorator.findDataByKey("test");

    assertEquals(1, testService.findCallCount, "Сервис должен быть вызван только один раз");
  }

  @Test
  void shouldUpdateCacheOnSave() {
    TestDataService testService = new TestDataService();
    CachingDecorator decorator = new CachingDecorator(testService);

    decorator.saveData("test", "data");
    Optional<String> result = decorator.findDataByKey("test");

    assertTrue(result.isPresent());
    assertEquals("data", result.get());
    assertEquals(0, testService.findCallCount, "Данные должны быть взяты из кэша, не вызывая сервис");
  }

  @Test
  void shouldInvalidateCacheOnDelete() {
    TestDataService testService = new TestDataService();
    CachingDecorator decorator = new CachingDecorator(testService);

    decorator.findDataByKey("test");
    decorator.deleteData("test");
    decorator.findDataByKey("test");

    assertEquals(2, testService.findCallCount, "Сервис должен быть вызван дважды после инвалидации кэша");
  }
}