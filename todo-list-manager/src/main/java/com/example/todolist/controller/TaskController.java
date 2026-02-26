package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public abstract class TaskController {

  private final TaskService taskService;
  private final TaskStatisticsService statisticsService;
  private final AppInfoService appInfoService;
  private final RequestScopedBean requestScopedBean;  // Прокси для request-scoped бина

  @Autowired
  public TaskController(
          TaskService taskService,
          TaskStatisticsService statisticsService,
          AppInfoService appInfoService,
          RequestScopedBean requestScopedBean) {
    this.taskService = taskService;
    this.statisticsService = statisticsService;
    this.appInfoService = appInfoService;
    this.requestScopedBean = requestScopedBean;
  }

  @GetMapping
  public List<Task> getAllTasks() {
    System.out.println("RequestScopedBean для этого запроса: " +
            requestScopedBean.getRequestInfo());
    return taskService.getAllTasks();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    return taskService.getTaskById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    PrototypeScopedBean prototypeBean = getPrototypeScopedBean();
    String generatedId = prototypeBean.generateTaskId();
    System.out.println("Использован prototype бин: " + prototypeBean.getBeanInfo());
    System.out.println("Сгенерирован ID для задачи: " + generatedId);

    Task createdTask = taskService.createTask(task);
    return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
    return taskService.updateTask(id, task)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    if (taskService.deleteTask(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/stats/repositories")
  public String getRepositoryStats() {
    return statisticsService.getRepositoryInfo();
  }

  @GetMapping("/cache/size")
  public String getCacheSize() {
    return String.format("Размер кэша задач: %d", taskService.getCacheSize());
  }

  @GetMapping("/info/app")
  public String getAppInfo() {
    return appInfoService.getAppInfo();
  }

  @GetMapping("/info/request")
  public String getRequestInfo() {
    return String.format(
            "<h3>Информация о текущем запросе</h3>" +
                    "<p>%s</p>" +
                    "<p><small>RequestScopedBean создается для каждого HTTP запроса</small></p>",
            requestScopedBean.getRequestInfo()
    );
  }

  @GetMapping("/info/prototype")
  public String getPrototypeInfo() {
    PrototypeScopedBean bean1 = getPrototypeScopedBean();
    PrototypeScopedBean bean2 = getPrototypeScopedBean();

    return String.format(
            "<h3>Демонстрация Prototype Scope</h3>" +
                    "<p><strong>Бин 1:</strong> %s</p>" +
                    "<p><strong>Бин 2:</strong> %s</p>" +
                    "<p><strong>Равны ли бины?</strong> %s</p>" +
                    "<p><small>Каждый вызов getPrototypeScopedBean() создает новый экземпляр</small></p>",
            bean1.getBeanInfo(),
            bean2.getBeanInfo(),
            (bean1 == bean2) ? "НЕТ (это разные объекты)" : "ДА (одинаковые)"
    );
  }

  @Lookup
  protected abstract PrototypeScopedBean getPrototypeScopedBean();
}