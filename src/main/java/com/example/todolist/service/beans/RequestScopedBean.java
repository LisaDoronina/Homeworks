package com.example.todolist.service.beans;


import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequestScope
public class RequestScopedBean {

  private final String requestId;
  private final Instant startedAt;

  public RequestScopedBean() {
    this.requestId = UUID.randomUUID().toString();
    this.startedAt = Instant.now();
  }

  public String getRequestId() {
    return requestId;
  }

  public Instant getStartedAt() {
    return startedAt;
  }
}