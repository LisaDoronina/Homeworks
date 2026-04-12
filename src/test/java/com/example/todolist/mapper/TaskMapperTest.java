package com.example.todolist.mapper;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

  private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

  @Test
  void shouldMapCreateDtoToEntity() {
    TaskCreateDto dto = new TaskCreateDto(
            "Test task",
            "Description",
            LocalDateTime.now(),
            Priority.HIGH,
            new HashSet<>()
    );

    Task task = mapper.toEntity(dto);

    assertNotNull(task, "Mapper returned null");
    assertEquals(dto.getTitle(), task.getTitle(), "Title not mapped");
    assertEquals(dto.getDescription(), task.getDescription(), "Description not mapped");
    assertEquals(dto.getDueDate(), task.getDueDate(), "DueDate not mapped");
    assertEquals(dto.getPriority(), task.getPriority(), "Priority not mapped");
    assertFalse(task.isCompleted(), "Completed should be false by default");
    assertNull(task.getId(), "ID should be null for new entity");
  }

  @Test
  void shouldMapEntityToResponseDto() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("Test");
    task.setDescription("Desc");
    task.setDueDate(LocalDateTime.now());
    task.setPriority(Priority.MEDIUM);
    task.setCompleted(true);
    task.setCreatedAt(LocalDateTime.now());

    TaskResponseDto dto = mapper.toResponseDto(task);

    assertNotNull(dto, "Mapper returned null");
    assertEquals(task.getId(), dto.getId(), "ID not mapped");
    assertEquals(task.getTitle(), dto.getTitle(), "Title not mapped");
    assertEquals(task.getDescription(), dto.getDescription(), "Description not mapped");
    assertEquals(task.getPriority(), dto.getPriority(), "Priority not mapped");
    assertEquals(task.getTags(), dto.getTags(), "Tags not mapped");
  }
}