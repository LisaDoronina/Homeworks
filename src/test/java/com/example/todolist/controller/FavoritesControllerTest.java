package com.example.todolist.controller;

import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.service.FavoritesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesController.class)
public class FavoritesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FavoritesService favoritesService;

  @Test
  void testAddFavorite() throws Exception {
    MockHttpSession session = new MockHttpSession();

    mockMvc.perform(post("/api/favorites/1").session(session))
            .andExpect(status().isOk());
  }

  @Test
  void testGetFavorites() throws Exception {
    MockHttpSession session = new MockHttpSession();

    TaskResponseDto taskDto = new TaskResponseDto(1L, "Test", "Desc", false,
            LocalDateTime.now(), LocalDate.now().plusDays(1), null, null);

    Mockito.when(favoritesService.getFavorites()).thenReturn(Set.of(1L, 2L));

    mockMvc.perform(get("/api/favorites").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
  }
}