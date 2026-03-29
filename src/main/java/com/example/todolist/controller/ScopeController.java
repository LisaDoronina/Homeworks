package com.example.todolist.controller;

import com.example.todolist.service.beans.PrototypeScopedBean;
import com.example.todolist.service.beans.RequestScopedBean;
import com.example.todolist.service.beans.PrototypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/scope")
@Tag(name = "Scope Demo", description = "Endpoints that demonstrate Spring bean scope behavior")
public class ScopeController {

  private final RequestScopedBean requestScopedBean;
  private final PrototypeService prototypeService;

  public ScopeController(RequestScopedBean requestScopedBean,
                             PrototypeService prototypeService) {
    this.requestScopedBean = requestScopedBean;
    this.prototypeService = prototypeService;
  }

  @Operation(
          summary = "Demonstrate request scope",
          description = "Shows that the same request-scoped bean instance is reused within one HTTP request."
  )
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Request scope information returned successfully",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(
                                  type = "object",
                                  example = "{\"requestId1\":\"abc\",\"requestId2\":\"abc\","
                                          + "\"sameInstanceWithinRequest\":true,"
                                          + "\"startedAt\":\"2026-03-22T12:30:00Z\"}"
                          )
                  )
          ),
          @ApiResponse(
                  responseCode = "500",
                  description = "Unexpected server error"
          )
  })
  @GetMapping("/request")
  public Map<String, Object> requestScope() {
    String id1 = requestScopedBean.getRequestId();
    Instant startedAt1 = requestScopedBean.getStartedAt();

    String id2 = requestScopedBean.getRequestId();
    Instant startedAt2 = requestScopedBean.getStartedAt();

    return Map.of(
            "requestId1", id1,
            "requestId2", id2,
            "sameInstanceWithinRequest", id1.equals(id2) && startedAt1.equals(startedAt2),
            "startedAt", startedAt1.toString());
  }

  @Operation(
          summary = "Demonstrate prototype scope",
          description = "Shows that each prototype bean lookup returns a different instance."
  )
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Prototype scope information returned successfully",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(
                                  type = "object",
                                  example = "{\"instanceId1\":\"a\",\"instanceId2\":\"b\","
                                          + "\"taskId1\":101,\"taskId2\":102,"
                                          + "\"differentInstances\":true}"
                          )
                  )
          ),
          @ApiResponse(
                  responseCode = "500",
                  description = "Unexpected server error"
          )
  })
  @GetMapping("/prototype")
  public Map<String, Object> prototypeScope() {
    PrototypeScopedBean b1 = prototypeService.newPrototypeBean();
    PrototypeScopedBean b2 = prototypeService.newPrototypeBean();

    return Map.of(
            "instanceId1", b1.getInstanceId(),
            "instanceId2", b2.getInstanceId(),
            "taskId1", b1.newTaskId(),
            "taskId2", b2.newTaskId(),
            "differentInstances", !b1.getInstanceId().equals(b2.getInstanceId()));
  }
}