package com.example.todolist.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

  private static final String VIEW_COOKIE = "viewPreference";

  @GetMapping("/view")
  public String getViewPreference(@CookieValue(name = VIEW_COOKIE, defaultValue = "detailed") String mode) {
    return mode;
  }

  @PostMapping("/view")
  public String setViewPreference(@RequestParam("mode") String mode, HttpServletResponse response) {
    if (!mode.equals("compact") && !mode.equals("detailed")) {
      throw new IllegalArgumentException("mode должен быть compact или detailed");
    }
    Cookie cookie = new Cookie(VIEW_COOKIE, mode);
    cookie.setPath("/");
    cookie.setHttpOnly(false);
    cookie.setMaxAge(7 * 24 * 60 * 60);
    response.addCookie(cookie);
    return mode;
  }
}