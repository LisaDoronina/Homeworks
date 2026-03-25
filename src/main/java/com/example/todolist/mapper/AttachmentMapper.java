package com.example.todolist.mapper;

import com.example.todolist.dto.attachement.AttachmentCreateDto;
import com.example.todolist.dto.attachement.AttachmentResponseDto;
import com.example.todolist.dto.attachement.AttachmentUpdateDto;
import com.example.todolist.model.TaskAttachment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "storedFileName", ignore = true)
  @Mapping(target = "uploadedAt", ignore = true)
  TaskAttachment toEntity(AttachmentCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  TaskAttachment updateEntity(AttachmentUpdateDto dto,
                              @MappingTarget TaskAttachment attachment);
  AttachmentResponseDto toResponseDto(TaskAttachment attachment);
}
