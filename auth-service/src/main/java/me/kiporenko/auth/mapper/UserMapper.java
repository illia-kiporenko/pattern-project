package me.kiporenko.auth.mapper;

import me.kiporenko.auth.domain.dto.UserDto;
import me.kiporenko.auth.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}