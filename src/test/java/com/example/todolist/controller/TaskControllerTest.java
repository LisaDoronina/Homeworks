package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.service.AppInfoService;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.TaskStatisticsService;
import com.example.todolist.service.RequestScopedBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @MockBean
  private TaskStatisticsService statisticsService;

  @MockBean
  private AppInfoService appInfoService;

  @MockBean
  private RequestScopedBean requestScopedBean;

  @MockBean
  private TaskMapper taskMapper;

  private Task task;
  private TaskResponseDto taskResponseDto;
  private TaskCreateDto taskCreateDto;
  private TaskUpdateDto taskUpdateDto;

  @BeforeEach
  void setup() {
    task = new Task(1L, "Test task", "Description", false, LocalDateTime.now(),
            LocalDate.now().plusDays(1), Priority.MEDIUM, Set.of("tag1"));
    taskResponseDto = new TaskResponseDto(1L, "Test task", "Description", false,
            task.getCreatedAt(), task.getDueDate(), task.getPriority(), task.getTags());
    taskCreateDto = new TaskCreateDto("Test task", "Description", LocalDate.now().plusDays(1),
            Priority.MEDIUM, Set.of("tag1"));
    taskUpdateDto = new TaskUpdateDto("Updated task", "Updated desc", true,
            LocalDate.now().plusDays(2), Priority.HIGH, Set.of("tag2"));
  }

  @Test
  void testGetAllTasks_success() throws Exception {
    Mockito.when(taskService.getAllTasks()).thenReturn(List.of(task));
    Mockito.when(taskMapper.toResponseDto(any())).thenReturn(taskResponseDto);

    mockMvc.perform(get("/api/tasks")
                    .cookie())
            .andExpect(status().isOk())
            .andExpect(header().exists("X-Total-Count"))
            .andExpect(header().string("X-API-Version", "2.0.0"))
            .andExpect(jsonPath("$[0].id").value(task.getId()));
  }

  @Test
  void testGetTaskById_found() throws Exception {
    Mockito.when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));
    Mockito.when(taskMapper.toResponseDto(task)).thenReturn(taskResponseDto);

    mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(task.getTitle()));
  }

  @Test
  void testGetTaskById_notFound() throws Exception {
    Mockito.when(taskService.getTaskById(2L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/tasks/2"))
            .andExpect(status().isNotFound());
  }

  @Test
  void testCreateTask_success() throws Exception {
    Mockito.when(taskMapper.toEntity(any())).thenReturn(task);
    Mockito.when(taskService.createTask(task)).thenReturn(task);
    Mockito.when(taskMapper.toResponseDto(task)).thenReturn(taskResponseDto);

    String json = """
                {
                    "title":"Test task",
                    "description":"Description",
                    "dueDate":"%s",
                    "priority":"MEDIUM",
                    "tags":["tag1"]
                }
                """.formatted(LocalDate.now().plusDays(1));

    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().string("X-API-Version", "2.0.0"))
            .andExpect(jsonPath("$.title").value("Test task"));
  }

  @Test
  void testUpdateTask_notFound() throws Exception {
    Mockito.when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

    String json = """
                {
                    "title":"Updated task"
                }
                """;

    mockMvc.perform(put("/api/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isNotFound());
  }
}
