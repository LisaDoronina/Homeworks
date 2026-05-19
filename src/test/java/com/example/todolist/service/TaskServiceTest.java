package com.example.todolist.service;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.FavoritesService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private FavoritesService favoritesService;

    @Test
    void updateTask_updatesStatusOfExistingTask() {
        // given
        Long taskId = 1L;

        Task existing = new Task();
        existing.setId(taskId);
        existing.setTitle("Initial Task");
        existing.setCompleted(false);
        existing.setPriority(Priority.LOW);

        Task updates = new Task();
        updates.setCompleted(true);
        updates.setPriority(Priority.HIGH);

        when(taskRepository.findAll()).thenReturn(List.of());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Task result = taskService.updateTask(taskId, updates);

        // then
        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);

        verify(taskRepository).findById(taskId);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().isCompleted()).isTrue();
        assertThat(captor.getValue().getPriority()).isEqualTo(Priority.HIGH);
    }
}
