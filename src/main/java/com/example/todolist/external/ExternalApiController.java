package com.example.todolist.external;

import com.example.todolist.dto.ExternalTaskCreateDto;
import com.example.todolist.dto.ExternalTaskDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
@Slf4j
public class ExternalApiController {

    private final ConcurrentHashMap<Long, ExternalTaskDto> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    /** Toggle to simulate server-side failures/delays for resilience testing */
    private final AtomicBoolean failMode = new AtomicBoolean(false);
    private volatile long delayMs = 0;

    @PostMapping("/fail-mode")
    public ResponseEntity<Map<String, Object>> setFailMode(
            @RequestParam boolean enabled,
            @RequestParam(defaultValue = "0") long delayMs) {
        this.failMode.set(enabled);
        this.delayMs = delayMs;
        log.info("External API fail-mode={}, delayMs={}", enabled, delayMs);
        return ResponseEntity.ok(Map.of("failMode", enabled, "delayMs", delayMs));
    }

    private ResponseEntity<?> maybeError() throws InterruptedException {
        if (delayMs > 0) Thread.sleep(delayMs);
        if (failMode.get()) {
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Simulated server error (fail-mode=true)"));
        }
        return null;
    }

    @PostMapping("/tasks")
    public ResponseEntity<ExternalTaskDto> createTask(@RequestBody ExternalTaskCreateDto dto,
                                                       HttpServletRequest request) {
        Long id = counter.getAndIncrement();
        ExternalTaskDto task = new ExternalTaskDto(id, dto.title(), dto.description(), dto.completed());
        store.put(id, task);

        URI location = ServletUriComponentsBuilder.fromRequestUri(request)
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        log.debug("Created external task id={} location={}", id, location);
        return ResponseEntity.created(location).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) throws InterruptedException {
        ResponseEntity<?> err = maybeError();
        if (err != null) return err;
        ExternalTaskDto task = store.get(id);
        if (task == null) {
            ProblemDetail problem = ProblemDetail.forStatus(404);
            problem.setTitle("Task Not Found");
            problem.setDetail("Task with id " + id + " was not found");
            return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problem);
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) throws InterruptedException {
        ResponseEntity<?> err = maybeError();
        if (err != null) return err;
        List<ExternalTaskDto> result = new ArrayList<>(store.values());

        if (completed != null) {
            boolean completedVal = completed;
            result = result.stream().filter(t -> t.completed() == completedVal).toList();
        }
        if (limit != null && limit > 0) {
            result = result.stream().limit(limit).toList();
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        if (!store.containsKey(id)) {
            ProblemDetail problem = ProblemDetail.forStatus(404);
            problem.setTitle("Task Not Found");
            problem.setDetail("Task with id " + id + " was not found");
            return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problem);
        }
        store.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        log.info("Unstable endpoint called with mode={}", mode);

        if ("timeout".equals(mode)) {
            Thread.sleep(30_000);
            return ResponseEntity.ok(Map.of("message", "finally responded after delay"));
        }

        if ("500".equals(mode)) {
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Internal Server Error", "message", "Simulated server error"));
        }

        if ("429".equals(mode)) {
            return ResponseEntity.status(429)
                    .header("Retry-After", "5")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Too Many Requests", "retryAfter", 5));
        }

        if ("html".equals(mode)) {
            return ResponseEntity.status(502)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>502 Bad Gateway</h1><p>Upstream error</p></body></html>");
        }

        return ResponseEntity.badRequest()
                .body(Map.of("error", "Unknown mode: " + mode,
                        "allowed", List.of("timeout", "500", "429", "html")));
    }
}
