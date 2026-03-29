package com.example.todolist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.example.todolist.service.AppInfoService;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToDoListAppTest {

  @Autowired
  private AppInfoService appInfoService;

  @Test
  void contextLoads() {
    assertNotNull(appInfoService);
  }

  @Test
  void customPropertiesAreInjected() {
    assertEquals("todo list manager", appInfoService.getAppName());
    assertEquals("2.0.0", appInfoService.getAppVersion());
  }
}
