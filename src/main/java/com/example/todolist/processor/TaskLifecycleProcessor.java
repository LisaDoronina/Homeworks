package com.example.todolist.processor;

import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

  private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
          throws BeansException {
    if (isInteresting(bean)) {
      log.info("[Bean lifecycle processor] BEFORE initialization: beanName='{}', type={}",
              beanName, bean.getClass().getName());
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
          throws BeansException {
    if (isInteresting(bean)) {
      log.info("[Bean lifecycle processor] AFTER initialization: beanName='{}', type={}",
              beanName, bean.getClass().getName());
    }
    return bean;
  }

  private boolean isInteresting(Object bean) {
    return (bean instanceof TaskService) || (bean instanceof TaskRepository);
  }
}