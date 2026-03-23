package com.example.todolist.controller;

import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.service.FavoritesService;
import com.example.todolist.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

  private final FavoritesService favoritesService;
  private final TaskService taskService;
  private final TaskMapper taskMapper;


  public FavoritesController(FavoritesService favoritesService, TaskService taskService, TaskMapper taskMapper) {
    this.favoritesService = favoritesService;
    this.taskService = taskService;
    this.taskMapper = taskMapper;
  }

  @PostMapping("/{taskId}")
  public void addFavorite(@PathVariable Long taskId) {
    favoritesService.addToFavorites(taskId);
  }

  @DeleteMapping("/{taskId}")
  public void removeFavorite(@PathVariable Long taskId) {
    favoritesService.removeFromFavorites(taskId);
  }

  @GetMapping
  public List<TaskResponseDto> getFavorites() {
    return favoritesService.getFavorites()
            .stream()
            .map(taskService::getTaskById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(taskMapper::toResponseDto)
            .collect(Collectors.toList());
  }
}
