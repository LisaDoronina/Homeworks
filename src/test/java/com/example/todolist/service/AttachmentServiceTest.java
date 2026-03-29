package com.example.todolist.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

import com.example.todolist.dto.attachement.AttachmentResponseDto;
import com.example.todolist.mapper.AttachmentMapper;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mapstruct.factory.Mappers;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

class AttachmentServiceTest {

  @TempDir
  Path tempDir;

  private TestTaskAttachmentRepository attachmentRepository;
  private TestTaskRepository taskRepository;
  private AttachmentMapper mapper;
  private AttachmentService service;
  private Path uploadDir;

  @BeforeEach
  void setUp() {
    attachmentRepository = new TestTaskAttachmentRepository();
    taskRepository = new TestTaskRepository();
    mapper = Mappers.getMapper(AttachmentMapper.class);
    uploadDir = tempDir.resolve("uploads");
  }

  @Test
  void shouldCreateDirectoryAndStoreAttachmentWhenSavingFileAndMetadata() throws Exception {

    createDefaultTask();
    service = createService(uploadDir.toString());
    service.init();

    MockMultipartFile file = new MockMultipartFile(
            "file",
            "notes.txt",
            "text/plain",
            "hello attachment".getBytes(UTF_8));

    AttachmentResponseDto response = service.storeAttachment(1L, file);

    assertTrue(Files.isDirectory(uploadDir));
    assertEquals("notes.txt", response.fileName());
    assertEquals("text/plain", response.contentType());
    assertEquals(file.getSize(), response.size());

    TaskAttachment saved = attachmentRepository.findById(response.id()).orElseThrow();
    Path storedFile = uploadDir.resolve(saved.getStoredFileName());
    assertTrue(Files.exists(storedFile));
    assertEquals("hello attachment", Files.readString(storedFile, UTF_8));
  }

  @Test
  void shouldReturnStoredMetadata() {
    service = createServiceWithTask();

    AttachmentResponseDto first = storeTestFile(1L, "notes.txt", "one");
    AttachmentResponseDto second = storeTestFile(1L, "report.txt", "two");

    TaskAttachment attachment = service.getAttachment(first.id());
    List<AttachmentResponseDto> attachments = service.getAttachmentsByTaskId(1L);

    assertEquals(first.id(), attachment.getId());
    assertEquals("notes.txt", attachment.getFileName());
    assertEquals(2, attachments.size());

    Set<Long> expectedIds = Set.of(first.id(), second.id());
    Set<Long> actualIds = attachments.stream()
            .map(AttachmentResponseDto::id)
            .collect(Collectors.toSet());
    assertEquals(expectedIds, actualIds);
  }

  @Test
  void shouldReturnStoredFileContents() throws Exception {

    service = createServiceWithTask();
    AttachmentResponseDto response = storeTestFile(1L, "guide.txt", "download me");

    Resource resource = service.loadAsResource(response.id());

    assertTrue(resource.exists());
    assertEquals("download me", Files.readString(resource.getFile().toPath(), UTF_8));
  }

  @Test
  void shouldRemoveFileAndMetadata() throws Exception {

    createDefaultTask();
    Path deleteUploadDir = tempDir.resolve("uploads-delete");
    service = createService(deleteUploadDir.toString());
    service.init();

    AttachmentResponseDto response = storeTestFile(1L, "trash.txt", "remove me");

    TaskAttachment saved = attachmentRepository.findById(response.id()).orElseThrow();
    Path storedFile = deleteUploadDir.resolve(saved.getStoredFileName());
    assertTrue(Files.exists(storedFile));

    service.deleteAttachment(response.id());

    assertFalse(Files.exists(storedFile));
    assertTrue(attachmentRepository.findById(response.id()).isEmpty());
  }

  @Test
  void shouldHandleMultipleFilesForSameTask() {
    service = createServiceWithTask();

    AttachmentResponseDto first = storeTestFile(1L, "file1.txt", "content1");
    AttachmentResponseDto second = storeTestFile(1L, "file2.txt", "content2");
    AttachmentResponseDto third = storeTestFile(1L, "file3.txt", "content3");

    List<AttachmentResponseDto> attachments = service.getAttachmentsByTaskId(1L);
    assertEquals(3, attachments.size());

    List<Long> ids = attachments.stream()
            .map(AttachmentResponseDto::id)
            .toList();

    assertTrue(ids.contains(first.id()));
    assertTrue(ids.contains(second.id()));
    assertTrue(ids.contains(third.id()));
  }

  private AttachmentService createService(String uploadPath) {
    return new AttachmentService(
            attachmentRepository,
            mapper,
            taskRepository,
            uploadPath);
  }

  private AttachmentService createServiceWithTask() {
    createDefaultTask();
    AttachmentService newService = createService(uploadDir.toString());
    newService.init();
    return newService;
  }

  private void createDefaultTask() {
    taskRepository.create(Task.builder()
            .title("Task with file")
            .description("Description")
            .completed(false)
            .createdAt(LocalDateTime.now())
            .dueDate(LocalDateTime.now().plusDays(1))
            .priority(Priority.MEDIUM)
            .tags(Set.of("files"))
            .build());
  }

  private AttachmentResponseDto storeTestFile(Long taskId, String fileName, String content) {
    MockMultipartFile file = new MockMultipartFile(
            "file",
            fileName,
            "text/plain",
            content.getBytes(UTF_8));
    return service.storeAttachment(taskId, file);
  }

  private static class TestTaskRepository implements TaskRepository {

    private final Map<Long, Task> storage = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Task create(Task task) {
      long id = sequence.incrementAndGet();
      task.setId(id);
      storage.put(id, task);
      return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
      return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Task> findAll() {
      return new ArrayList<>(storage.values());
    }

    @Override
    public Task update(Task task) {
      storage.put(task.getId(), task);
      return task;
    }

    @Override
    public boolean deleteById(Long id) {
      return storage.remove(id) != null;
    }
  }

  private static class TestTaskAttachmentRepository implements TaskAttachmentRepository {

    private final Map<Long, TaskAttachment> storage = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public TaskAttachment create(TaskAttachment taskAttachment) {
      long id = sequence.incrementAndGet();
      taskAttachment.setId(id);
      storage.put(id, taskAttachment);
      return taskAttachment;
    }

    @Override
    public Optional<TaskAttachment> findById(Long id) {
      return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TaskAttachment> findAllAttachmentsByTaskId(Long taskId) {
      return storage.values().stream()
              .filter(attachment -> attachment.getTaskId().equals(taskId))
              .toList();
    }

    @Override
    public TaskAttachment update(TaskAttachment taskAttachment) {
      storage.put(taskAttachment.getId(), taskAttachment);
      return taskAttachment;
    }

    @Override
    public boolean deleteById(Long id) {
      return storage.remove(id) != null;
    }
  }
}