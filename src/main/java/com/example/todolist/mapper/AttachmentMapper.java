package com.example.todolist.mapper;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.model.TaskAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

  @Mapping(target = "taskId", source = "task.id")
  AttachmentResponseDto toResponseDto(TaskAttachment attachment);

  List<AttachmentResponseDto> toResponseDtoList(List<TaskAttachment> attachments);
}