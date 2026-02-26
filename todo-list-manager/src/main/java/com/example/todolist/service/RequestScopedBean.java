package com.example.todolist.service;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {

  private static final Logger logger = LoggerFactory.getLogger(RequestScopedBean.class);

  private final String requestId;
  private final LocalDateTime creationTime;
  private final String requestTimestamp;

  public RequestScopedBean() {
    this.requestId = UUID.randomUUID().toString().substring(0, 8);
    this.creationTime = LocalDateTime.now();
    this.requestTimestamp = creationTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

    logger.info("[{}] Создан новый RequestScopedBean для запроса", requestId);
  }

  @PostConstruct
  public void init() {
    logger.info("[{}] RequestScopedBean инициализирован", requestId);
  }

  @PreDestroy
  public void destroy() {
    logger.info("[{}] RequestScopedBean уничтожен (запрос обработан)", requestId);
  }

  public String getRequestInfo() {
    return String.format(
            "Request ID: %s, Время создания: %s",
            requestId, requestTimestamp
    );
  }

  public String getRequestId() {
    return requestId;
  }

  public LocalDateTime getCreationTime() {
    return creationTime;
  }
}