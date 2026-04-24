package com.example.todolist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.external-api.base-url}")
    private String baseUrl;

    @Value("${app.external-api.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${app.external-api.read-timeout-ms:5000}")
    private int readTimeoutMs;

    @Bean
    public RestClient externalTasksRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "todo-list-gateway/1.0")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
