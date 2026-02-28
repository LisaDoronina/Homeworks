package com.example.todolist.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

@Component
@Scope("prototype")
public class PrototypeScopedBean {

  private static final Logger logger = LoggerFactory.getLogger(PrototypeScopedBean.class);

  private final String beanId;
  private int counter = 0;

  public PrototypeScopedBean() {
    this.beanId = UUID.randomUUID().toString().substring(0, 6);
    logger.info("[{}] Создан новый PrototypeScopedBean (прототип)", beanId);
  }

  @PostConstruct
  public void init() {
    logger.info("[{}] PrototypeScopedBean инициализирован", beanId);
  }

  @PreDestroy
  public void destroy() {
    logger.info("[{}] PrototypeScopedBean уничтожен", beanId);
  }

  public String generateTaskId() {
    counter++;
    String taskId = "TASK-" + UUID.randomUUID().toString().substring(0, 8);
    logger.debug("[{}] Сгенерирован ID задачи #{}: {}", beanId, counter, taskId);
    return taskId;
  }

  public String getBeanInfo() {
    return String.format(
            "PrototypeBean ID: %s, сгенерировано ID задач: %d",
            beanId, counter
    );
  }
}