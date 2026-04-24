package com.example.todolist.config;

import com.example.todolist.security.PepperedPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Value("${app.security.pepper}")
    private String pepper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PepperedPasswordEncoder(new BCryptPasswordEncoder(), pepper);
    }
}
