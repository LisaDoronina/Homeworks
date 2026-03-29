package com.example.todolist.dto.task;

import com.example.todolist.model.Priority;
import com.example.todolist.validation.OnUpdate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDto {

  @Size(groups = OnUpdate.class, min = 3, max = 100, message = "Title must be between 3 and 100 characters")
  private String title;

  @Size(groups = OnUpdate.class, max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  private Boolean completed;

  @FutureOrPresent(groups = OnUpdate.class, message = "Due date must be today or in the future")
  private LocalDate dueDate;

  private Priority priority;

  @Size(groups = OnUpdate.class, max = 5, message = "Cannot have more than 5 tags")
  private Set<@Size(groups = OnUpdate.class, min = 1, message = "Tag cannot be blank") String> tags;
}