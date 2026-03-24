package com.example.todolist.controller;

import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.service.FavoritesService;
import com.example.todolist.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesController.class)
class FavoritesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FavoritesService favoritesService;

  @MockBean
  private TaskService taskService;

  @Test
  void shouldAddToFavorites() throws Exception {
    mockMvc.perform(post("/api/favorites/1"))
            .andExpect(status().isOk());
  }

  @Test
  void shouldRemoveFromFavorites() throws Exception {
    mockMvc.perform(delete("/api/favorites/1"))
            .andExpect(status().isOk());
  }

  @Test
  void shouldReturnFavorites() throws Exception {

    Mockito.when(favoritesService.getFavorites())
            .thenReturn(Set.of(1L, 2L));

    mockMvc.perform(get("/api/favorites"))
            .andExpect(status().isOk());
  }

  @Test
  void shouldReturnEmptyFavorites() throws Exception {

    Mockito.when(favoritesService.getFavorites())
            .thenReturn(Set.of());

    mockMvc.perform(get("/api/favorites"))
            .andExpect(status().isOk())
            .andExpect(content().string("[]"));
  }
}