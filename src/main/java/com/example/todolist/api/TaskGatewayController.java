package com.example.todolist.api;

import com.example.todolist.dto.ExternalTaskCreateDto;
import com.example.todolist.dto.ExternalTaskDto;
import com.example.todolist.service.TasksGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskGatewayController {

    private final TasksGatewayService tasksGatewayService;

    @PostMapping
    public ResponseEntity<ExternalTaskDto> createTask(@Valid @RequestBody ExternalTaskCreateDto dto) {
        return tasksGatewayService.createTask(dto);
    }

    @GetMapping("/{id}")
    public ExternalTaskDto getTask(@PathVariable Long id) {
        return tasksGatewayService.getTask(id);
    }

    @GetMapping
    public List<ExternalTaskDto> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        return tasksGatewayService.listTasks(completed, limit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return tasksGatewayService.deleteTask(id);
    }
}
