package com.example.todolist.mapper;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

  private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

  @Test
  void shouldMapCreateDtoToEntity() {
    TaskCreateDto dto = new TaskCreateDto(
            "Test task",
            "Description",
            LocalDate.now(),
            Priority.HIGH,
            Set.of("tag1")
    );

    Task task = mapper.toEntity(dto);

    assertEquals(dto.getTitle(), task.getTitle());
    assertEquals(dto.getDescription(), task.getDescription());
    assertEquals(dto.getDueDate(), task.getDueDate());
    assertEquals(dto.getPriority(), task.getPriority());
  }

  @Test
  void shouldMapEntityToResponseDto() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("Test");
    task.setCreatedAt(LocalDateTime.now());

    TaskResponseDto dto = mapper.toResponseDto(task);

    assertEquals(task.getId(), dto.getId());
    assertEquals(task.getTitle(), dto.getTitle());
  }
}