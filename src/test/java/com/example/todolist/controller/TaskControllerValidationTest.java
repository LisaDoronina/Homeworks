package com.example.todolist.controller;

import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.service.TaskService;
import com.example.todolist.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @MockBean
  private TaskMapper taskMapper;

  @Test
  void shouldReturn400WhenInvalidCreateDto() throws Exception {

    String invalidJson = """
        {
          "title": "",
          "priority": null
        }
        """;

    mockMvc.perform(post("/api/tasks")
                    .contentType("application/json")
                    .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.details.title").exists());
  }

  @Test
  void shouldReturn400WhenMissingParam() throws Exception {
    mockMvc.perform(post("/api/preferences/view"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"));
  }

  @Test
  void shouldReturn400WhenInvalidJson() throws Exception {

    String brokenJson = "{ invalid json }";

    mockMvc.perform(post("/api/tasks")
                    .contentType("application/json")
                    .content(brokenJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Malformed JSON request"));
  }

  @Test
  void shouldReturn404WhenTaskNotFound() throws Exception {

    Mockito.when(taskService.getTaskById(1L))
            .thenThrow(new TaskNotFoundException(1L));

    mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Task not found with id: 1"));
  }

  @Test
  void shouldReturn400OnConstraintViolation() throws Exception {
    mockMvc.perform(get("/api/tasks/-1"))
            .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn500OnUnexpectedError() throws Exception {

    Mockito.when(taskService.getAllTasks())
            .thenThrow(new RuntimeException("Boom"));

    mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500));
  }
}