package com.example.todolist.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication auth) {
        return Map.of(
                "username", auth.getName(),
                "authorities", auth.getAuthorities().toString(),
                "message", "Welcome to your profile"
        );
    }

    @GetMapping("/docs")
    public Map<String, Object> docs() {
        return Map.of(
                "version", "1.0.0",
                "description", "Internal API documentation — requires READ_PRIVILEGE"
        );
    }
}
