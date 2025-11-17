package com.mipt.elizavetadoronina.patternsClasses;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CachingDecorator implements DataService {
  private final DataService wrapped;
  private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

  public CachingDecorator(DataService wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    String cached = cache.get(key);
    if (cached != null) {
      return Optional.of(cached);
    }

    Optional<String> result = wrapped.findDataByKey(key);
    result.ifPresent(data -> cache.put(key, data));
    return result;
  }

  @Override
  public void saveData(String key, String data) {
    cache.put(key, data);
    wrapped.saveData(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    cache.remove(key);
    return wrapped.deleteData(key);
  }
}