package com.example.todolist.controller;

import com.example.todolist.service.TaskStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Read-only statistics and demo metrics")
public class StatisticsController {

  private final TaskStatisticsService statisticsService;

  public StatisticsController(TaskStatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @Operation(
          summary = "Compare repositories",
          description = "Returns a short comparison string for the primary and stub task repositories."
  )
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Statistics returned successfully",
                  content = @Content(
                          mediaType = MediaType.APPLICATION_JSON_VALUE,
                          schema = @Schema(type = "object",
                                  example = "{\"comparison\":\"primary=3, stub=0\"}")
                  )
          ),
          @ApiResponse(
                  responseCode = "500",
                  description = "Unexpected server error"
          )
  })
  @GetMapping
  public ResponseEntity<Map<String, String>> compare() {
    return ResponseEntity.ok(Map.of("comparison", statisticsService.compareRepositories()));
  }
}