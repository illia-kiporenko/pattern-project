package me.kiporenko.system.mapper;

import me.kiporenko.system.model.Post;
import me.kiporenko.system.model.dto.PostResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponseDTO toDto(Post post);
}