package me.kiporenko.system.mapper;

import me.kiporenko.system.model.Blog;
import me.kiporenko.system.model.dto.BlogResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogMapper {
    /**
     * Converts a Blog entity to a BlogResponseDTO.
     * MapStruct will automatically map fields with the same name.
     */
    BlogResponseDTO toDto(Blog blog);
}