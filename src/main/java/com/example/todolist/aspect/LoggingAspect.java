package com.example.todolist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("within(@org.springframework.stereotype.Service *)")
  public void serviceMethods() {}

  @Around("serviceMethods()")
  public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    logger.info("{}.{}() - НАЧАЛО", className, methodName);
    logger.debug("Параметры: {}", args.length > 0 ? Arrays.toString(args) : "нет");

    long startTime = System.currentTimeMillis();
    Object result = null;

    try {
      result = joinPoint.proceed();

      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      if (result != null) {
        logger.info("{}.{}() - КОНЕЦ (время: {} мс)", className, methodName, duration);
        logger.debug("Результат: {}", result);
      } else {
        logger.info("{}.{}() - КОНЕЦ (время: {} мс, результат: void)",
                className, methodName, duration);
      }

      return result;

    } catch (Exception e) {
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      logger.error("{}.{}() - ИСКЛЮЧЕНИЕ (время: {} мс)",
              className, methodName, duration);
      logger.error("Тип исключения: {}, сообщение: {}",
              e.getClass().getSimpleName(), e.getMessage());

      throw e;
    }
  }
}