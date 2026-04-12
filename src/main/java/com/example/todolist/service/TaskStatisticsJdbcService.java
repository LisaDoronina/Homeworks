package com.example.todolist.service;

import com.example.todolist.dto.TaskPriorityCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskStatisticsJdbcService {

  private final JdbcTemplate jdbcTemplate;

  public List<TaskPriorityCountDto> getTasksCountByPriority() {
    String sql = """
            SELECT priority, COUNT(*) as count 
            FROM tasks 
            GROUP BY priority 
            ORDER BY 
                CASE priority 
                    WHEN 'URGENT' THEN 1 
                    WHEN 'HIGH' THEN 2 
                    WHEN 'MEDIUM' THEN 3 
                    WHEN 'LOW' THEN 4 
                END
            """;

    return jdbcTemplate.query(sql, new TaskPriorityCountRowMapper());
  }

  private static class TaskPriorityCountRowMapper implements RowMapper<TaskPriorityCountDto> {
    @Override
    public TaskPriorityCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new TaskPriorityCountDto(
              rs.getString("priority"),
              rs.getLong("count")
      );
    }
  }

  public Map<String, Object> getGeneralStatistics() {
    String sql = """
            SELECT 
                COUNT(*) as total_tasks,
                COUNT(CASE WHEN completed = true THEN 1 END) as completed_tasks,
                COUNT(CASE WHEN completed = false THEN 1 END) as pending_tasks,
                COUNT(CASE WHEN due_date < NOW() AND completed = false THEN 1 END) as overdue_tasks,
                AVG(CASE WHEN size IS NOT NULL THEN size ELSE 0 END) as avg_attachment_size
            FROM tasks t
            LEFT JOIN task_attachments ta ON t.id = ta.task_id
            """;

    return jdbcTemplate.queryForMap(sql);
  }

  public long getTasksCreatedInLastDays(int days) {
    String sql = """
            SELECT COUNT(*) 
            FROM tasks 
            WHERE created_at >= NOW() - INTERVAL ? DAY
            """;

    return jdbcTemplate.queryForObject(sql, Long.class, days);
  }

  public double getAverageTagsPerTask() {
    String sql = """
            SELECT AVG(array_length(string_to_array(tags, ','), 1)) 
            FROM tasks 
            WHERE tags IS NOT NULL
            """;

    Double result = jdbcTemplate.queryForObject(sql, Double.class);
    return result != null ? result : 0.0;
  }

  public List<Map<String, Object>> getAttachmentStatistics() {
    String sql = """
            SELECT 
                t.id as task_id,
                t.title as task_title,
                COUNT(ta.id) as attachments_count,
                COALESCE(SUM(ta.size), 0) as total_size
            FROM tasks t
            LEFT JOIN task_attachments ta ON t.id = ta.task_id
            GROUP BY t.id, t.title
            ORDER BY attachments_count DESC
            """;

    return jdbcTemplate.queryForList(sql);
  }

  public List<Map<String, Object>> getDetailedPriorityStatistics() {
    String sql = """
            SELECT 
                priority,
                COUNT(*) as total,
                COUNT(CASE WHEN completed = true THEN 1 END) as completed,
                COUNT(CASE WHEN completed = false THEN 1 END) as pending,
                ROUND(COUNT(CASE WHEN completed = true THEN 1 END) * 100.0 / COUNT(*), 2) as completion_rate
            FROM tasks 
            GROUP BY priority 
            ORDER BY 
                CASE priority 
                    WHEN 'URGENT' THEN 1 
                    WHEN 'HIGH' THEN 2 
                    WHEN 'MEDIUM' THEN 3 
                    WHEN 'LOW' THEN 4 
                END
            """;

    return jdbcTemplate.queryForList(sql);
  }
}