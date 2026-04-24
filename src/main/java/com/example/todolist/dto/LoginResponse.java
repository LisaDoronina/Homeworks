package com.example.todolist.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {}
