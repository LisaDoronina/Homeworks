package com.example.todolist.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AppInfoService {

  private static final Logger logger = LoggerFactory.getLogger(AppInfoService.class);

  @Value("${app.name}")
  private String appName;

  @Value("${app.version}")
  private String appVersion;

  @Value("${app.description}")
  private String appDescription;

  @Value("${app.developer}")
  private String developer;

  @Value("${server.port}")
  private int serverPort;

  @PostConstruct
  public void init() {
    logger.info("📱 Информация о приложении (инжектировано через @Value):");
    logger.info("   Название: {}", appName);
    logger.info("   Версия: {}", appVersion);
    logger.info("   Описание: {}", appDescription);
    logger.info("   Разработчик: {}", developer);
    logger.info("   Порт: {}", serverPort);
  }

  public String getAppInfo() {
    return String.format(
            "<h2>Информация о приложении</h2>" +
                    "<ul>" +
                    "<li><strong>Название:</strong> %s</li>" +
                    "<li><strong>Версия:</strong> %s</li>" +
                    "<li><strong>Описание:</strong> %s</li>" +
                    "<li><strong>Разработчик:</strong> %s</li>" +
                    "<li><strong>Порт:</strong> %d</li>" +
                    "</ul>",
            appName, appVersion, appDescription, developer, serverPort
    );
  }
}