package com.example.todolist.processor;

import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

  private static final Logger logger = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      logger.info("BEFORE INIT - Бин {} типа {} проходит инициализацию",
              beanName, bean.getClass().getSimpleName());
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      logger.info("AFTER INIT - Бин {} типа {} успешно инициализирован",
              beanName, bean.getClass().getSimpleName());
    }
    return bean;
  }
}