package com.example.todolist.controller;

import com.example.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void testCreateTask_Positive() {
    Task newTask = new Task(null, "Тестовая задача", "Тестовое описание", false);

    ResponseEntity<Task> response = restTemplate.postForEntity(
            "/api/tasks", newTask, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("Тестовая задача");
  }

  @Test
  public void testCreateTask_Negative() {
    ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/tasks", null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testGetAllTasks_Positive() {
    ResponseEntity<Task[]> response = restTemplate.getForEntity(
            "/api/tasks", Task[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void testGetTaskById_Positive() {
    Task newTask = new Task(null, "Задача для поиска", "Описание", false);
    ResponseEntity<Task> createResponse = restTemplate.postForEntity(
            "/api/tasks", newTask, Task.class);
    Long taskId = createResponse.getBody().getId();

    ResponseEntity<Task> response = restTemplate.getForEntity(
            "/api/tasks/" + taskId, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(taskId);
  }

  @Test
  public void testGetTaskById_Negative() {
    ResponseEntity<Task> response = restTemplate.getForEntity(
            "/api/tasks/99999", Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void testUpdateTask_Positive() {
    Task newTask = new Task(null, "Старое название", "Старое описание", false);
    ResponseEntity<Task> createResponse = restTemplate.postForEntity(
            "/api/tasks", newTask, Task.class);
    Long taskId = createResponse.getBody().getId();

    Task updatedTask = new Task(taskId, "Новое название", "Новое описание", true);
    HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask);

    ResponseEntity<Task> response = restTemplate.exchange(
            "/api/tasks/" + taskId, HttpMethod.PUT, requestEntity, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getTitle()).isEqualTo("Новое название");
    assertThat(response.getBody().isCompleted()).isTrue();
  }

  @Test
  public void testUpdateTask_Negative() {
    Task updatedTask = new Task(99999L, "Несуществующая", "Описание", true);
    HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask);

    ResponseEntity<Task> response = restTemplate.exchange(
            "/api/tasks/99999", HttpMethod.PUT, requestEntity, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void testDeleteTask_Positive() {
    Task newTask = new Task(null, "Задача для удаления", "Будет удалена", false);
    ResponseEntity<Task> createResponse = restTemplate.postForEntity(
            "/api/tasks", newTask, Task.class);
    Long taskId = createResponse.getBody().getId();

    ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tasks/" + taskId, HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<Task> checkResponse = restTemplate.getForEntity(
            "/api/tasks/" + taskId, Task.class);
    assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void testDeleteTask_Negative() {
    ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tasks/99999", HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}