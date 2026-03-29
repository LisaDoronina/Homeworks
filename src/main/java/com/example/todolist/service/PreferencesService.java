package com.example.todolist.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class PreferencesService {

  private static final String VIEW_PREFERENCE_COOKIE_NAME = "viewPreference";
  private static final String DEFAULT_VIEW_MODE = "detailed";
  private static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60;

  public String getViewPreference(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Optional<Cookie> viewPreferenceCookie = Arrays.stream(cookies)
              .filter(cookie -> VIEW_PREFERENCE_COOKIE_NAME.equals(cookie.getName()))
              .findFirst();

      if (viewPreferenceCookie.isPresent()) {
        String value = viewPreferenceCookie.get().getValue();
        log.debug("View preference found: {}", value);
        return value;
      }
    }

    log.debug("No view preference cookie found, using default: {}", DEFAULT_VIEW_MODE);
    return DEFAULT_VIEW_MODE;
  }

  public void setViewPreference(String mode, HttpServletResponse response) {

    if (!"compact".equals(mode) && !"detailed".equals(mode)) {
      throw new IllegalArgumentException("Invalid view mode. Allowed values: compact, detailed");
    }

    Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE_NAME, mode);
    cookie.setPath("/");
    cookie.setMaxAge(COOKIE_MAX_AGE);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);

    response.addCookie(cookie);
    log.info("View preference set to: {}", mode);
  }

  public void resetViewPreference(HttpServletResponse response) {
    Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE_NAME, null);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);

    response.addCookie(cookie);
    log.info("View preference cookie removed");
  }

  public String getAllCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null || cookies.length == 0) {
      return "No cookies found";
    }

    StringBuilder sb = new StringBuilder();
    for (Cookie cookie : cookies) {
      sb.append(String.format("Name: %s, Value: %s, Path: %s, MaxAge: %d\n",
              cookie.getName(),
              cookie.getValue(),
              cookie.getPath(),
              cookie.getMaxAge()));
    }
    return sb.toString();
  }
}