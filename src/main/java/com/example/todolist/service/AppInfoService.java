package com.example.todolist.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppInfoService {

  private final String appName;
  private final String appVersion;

  public AppInfoService(
          @Value("${app.name:DefaultAppName}") String appName,
          @Value("${app.version:2.0.0}") String appVersion) {
    this.appName = appName;
    this.appVersion = appVersion;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppVersion() {
    return appVersion;
  }
}