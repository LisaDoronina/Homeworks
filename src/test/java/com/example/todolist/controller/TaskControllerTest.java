package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import com.example.todolist.security.JwtUtils;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.TaskStatisticsJdbcService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Order(1)
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private TaskStatisticsJdbcService taskStatisticsJdbcService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void createTask_returns201AndResponseBody() throws Exception {
        // given
        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("New Task Title");
        createDto.setPriority(Priority.HIGH);
        createDto.setDueDate(LocalDateTime.now().plusDays(5));

        Task task = new Task();
        task.setId(1L);
        task.setTitle("New Task Title");
        task.setCompleted(false);
        task.setPriority(Priority.HIGH);

        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("New Task Title");
        responseDto.setCompleted(false);
        responseDto.setPriority("HIGH");
        responseDto.setAttachments(List.of());

        when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(task);
        when(taskService.createTask(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponseDto(any(Task.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task Title"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.attachments").isArray());
    }

    @Test
    void getTaskById_existingTask_returns200AndResponseBody() throws Exception {
        // given
        Long taskId = 42L;

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Existing Task");
        task.setCompleted(false);
        task.setPriority(Priority.MEDIUM);

        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setId(taskId);
        responseDto.setTitle("Existing Task");
        responseDto.setCompleted(false);
        responseDto.setPriority("MEDIUM");
        responseDto.setAttachments(List.of());

        when(taskService.getTaskById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponseDto(any(Task.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.title").value("Existing Task"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.attachments").isArray());
    }

    @Test
    void createTask_missingBody_returns400() throws Exception {
        // when & then — @RequestBody обязателен, пустой запрос должен вернуть 400
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
