package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
        // Маленький пул — быстрое закрытие при shutdown, без HikariCP warnings
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "2");
        registry.add("spring.datasource.hikari.validation-timeout", () -> "1000");
    }

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findTasksDueWithinNextSevenDays_returnsOnlyTasksDueSoon() {
        // given
        Task dueSoon = new Task();
        dueSoon.setTitle("Due Soon Task");
        dueSoon.setDescription("Will be due in 3 days");
        dueSoon.setCompleted(false);
        dueSoon.setPriority(Priority.HIGH);
        dueSoon.setDueDate(LocalDateTime.now().plusDays(3));

        Task dueLater = new Task();
        dueLater.setTitle("Due Later Task");
        dueLater.setDescription("Will be due in 14 days");
        dueLater.setCompleted(false);
        dueLater.setPriority(Priority.LOW);
        dueLater.setDueDate(LocalDateTime.now().plusDays(14));

        Task overdue = new Task();
        overdue.setTitle("Overdue Task");
        overdue.setDescription("Was due yesterday");
        overdue.setCompleted(false);
        overdue.setPriority(Priority.URGENT);
        overdue.setDueDate(LocalDateTime.now().minusDays(1));

        taskRepository.saveAll(List.of(dueSoon, dueLater, overdue));
        entityManager.flush();
        entityManager.clear();

        // when
        LocalDateTime now = LocalDateTime.now();
        List<Task> result = taskRepository.findTasksDueWithinNextSevenDays(now, now.plusDays(7));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Due Soon Task");
    }
}
