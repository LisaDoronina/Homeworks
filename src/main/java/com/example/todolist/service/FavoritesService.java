package com.example.todolist.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class FavoritesService {

  private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

  private final HttpSession session;

  public FavoritesService(HttpSession session) {
    this.session = session;
  }

  @SuppressWarnings("unchecked")
  private Set<Long> getFavoriteSet() {
    var attribute = (Set<Long>) session.getAttribute(FAVORITES_SESSION_KEY);
    if (attribute == null) {
      attribute = new HashSet<>();
      session.setAttribute(FAVORITES_SESSION_KEY, attribute);
    }
    return attribute;
  }

  public void addToFavorites(Long taskId) {
    getFavoriteSet().add(taskId);
  }

  public void removeFromFavorites(Long taskId) {
    getFavoriteSet().remove(taskId);
  }

  public Set<Long> getFavorites() {
    return getFavoriteSet();
  }
}