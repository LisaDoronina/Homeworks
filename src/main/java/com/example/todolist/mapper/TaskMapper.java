package com.example.todolist.mapper;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {

  @Autowired
  protected AttachmentMapper attachmentMapper;

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "completed", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "attachments", ignore = true)
  @Mapping(target = "tags", source = "tags", qualifiedByName = "setToString")
  public abstract Task toEntity(TaskCreateDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "attachments", ignore = true)
  @Mapping(target = "tags", source = "tags", qualifiedByName = "setToString")
  public abstract Task updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

  @Mapping(target = "priority", expression = "java(task.getPriority().name())")
  @Mapping(target = "tags", source = "task", qualifiedByName = "stringToSet")
  @Mapping(target = "attachments", source = "attachments")
  public abstract TaskResponseDto toResponseDto(Task task);

  protected List<AttachmentResponseDto> mapAttachments(List<com.example.todolist.model.TaskAttachment> attachments) {
    if (attachments == null) {
      return List.of();
    }
    return attachmentMapper.toResponseDtoList(attachments);
  }

  @Named("setToString")
  protected String setToString(Set<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return null;
    }
    return String.join(",", tags);
  }

  @Named("stringToSet")
  protected Set<String> stringToSet(Task task) {
    if (task.getTags() == null || task.getTags().isEmpty()) {
      return Set.of();
    }
    return Set.of(task.getTags().split(","));
  }
}