package com.example.todolist.service.beans;

import com.example.todolist.service.beans.PrototypeScopedBean;
import com.example.todolist.service.beans.RequestScopedBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class PrototypeService {

  private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;

  public PrototypeService(ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
    this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
  }

  public PrototypeScopedBean newPrototypeBean() {
    return prototypeScopedBeanProvider.getObject();
  }
}