package com.example.todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreferencesController.class)
class PreferencesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnDefaultViewPreference() throws Exception {
    mockMvc.perform(get("/api/preferences/view"))
            .andExpect(status().isOk())
            .andExpect(content().string("detailed"));
  }

  @Test
  void shouldReturnCookieValue() throws Exception {
    mockMvc.perform(get("/api/preferences/view")
                    .cookie(new Cookie("viewPreference", "compact")))
            .andExpect(status().isOk())
            .andExpect(content().string("compact"));
  }

  @Test
  void shouldSetViewPreference() throws Exception {
    mockMvc.perform(post("/api/preferences/view")
                    .param("mode", "compact"))
            .andExpect(status().isOk())
            .andExpect(cookie().value("viewPreference", "compact"));
  }

  @Test
  void shouldFailOnInvalidMode() throws Exception {
    mockMvc.perform(post("/api/preferences/view")
                    .param("mode", "invalid"))
            .andExpect(status().isInternalServerError());
  }
}