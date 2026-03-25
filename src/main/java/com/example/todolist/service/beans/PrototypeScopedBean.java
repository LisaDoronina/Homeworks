package com.example.todolist.service.beans;

import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope("prototype")
public class PrototypeScopedBean {

  private final String instanceId;

  public PrototypeScopedBean() {
    this.instanceId = UUID.randomUUID().toString();
  }

  public String newTaskId() {
    return "new task id is " + instanceId;
  }

  public String getInstanceId() {
    return instanceId;
  }
}