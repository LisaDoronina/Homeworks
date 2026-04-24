package com.example.todolist.client;

import com.example.todolist.dto.ExternalTaskCreateDto;
import com.example.todolist.dto.ExternalTaskDto;
import com.example.todolist.exception.ExternalApiException;
import com.example.todolist.exception.TaskNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExternalTasksClient {

    private final RestClient externalTasksRestClient;
    private final ObjectMapper objectMapper;

    public ResponseEntity<ExternalTaskDto> createTask(ExternalTaskCreateDto dto) {
        return externalTasksRestClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ExternalApiException("Client error creating task", res.getStatusCode().value());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    handleServerError(res);
                })
                .toEntity(ExternalTaskDto.class);
    }

    public ExternalTaskDto getTask(Long id) {
        return externalTasksRestClient.get()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    String detail = parseProblemDetail(res);
                    throw new TaskNotFoundException("External task not found (id=" + id + "): " + detail);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    handleServerError(res);
                })
                .body(ExternalTaskDto.class);
    }

    public List<ExternalTaskDto> listTasks(Boolean completed, Integer limit) {
        return externalTasksRestClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/tasks");
                    if (completed != null) builder = builder.queryParam("completed", completed);
                    if (limit != null) builder = builder.queryParam("limit", limit);
                    return builder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    handleServerError(res);
                })
                .body(new ParameterizedTypeReference<List<ExternalTaskDto>>() {});
    }

    public ResponseEntity<Void> deleteTask(Long id) {
        return externalTasksRestClient.delete()
                .uri("/tasks/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    String detail = parseProblemDetail(res);
                    throw new TaskNotFoundException("External task not found (id=" + id + "): " + detail);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    handleServerError(res);
                })
                .toBodilessEntity();
    }

    private void handleServerError(org.springframework.http.client.ClientHttpResponse res) throws IOException {
        int status = res.getStatusCode().value();
        MediaType contentType = res.getHeaders().getContentType();

        if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_HTML)) {
            byte[] snippet = res.getBody().readNBytes(200);
            log.warn("External API returned HTML body (status={}): {}",
                    status, new String(snippet, StandardCharsets.UTF_8));
            throw new ExternalApiException("External API returned unexpected HTML response", status);
        }

        throw new ExternalApiException("External API server error: " + status, status);
    }

    private String parseProblemDetail(org.springframework.http.client.ClientHttpResponse res) {
        try {
            byte[] body = res.getBody().readAllBytes();
            JsonNode node = objectMapper.readTree(body);
            return node.has("detail") ? node.get("detail").asText() : "not found";
        } catch (Exception e) {
            log.warn("Failed to parse ProblemDetail from 404 response", e);
            return "unknown";
        }
    }
}
