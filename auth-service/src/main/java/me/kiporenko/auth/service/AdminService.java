package me.kiporenko.auth.service;

import lombok.RequiredArgsConstructor;
import me.kiporenko.auth.domain.model.Role;
import me.kiporenko.auth.domain.dto.UserDto;
import me.kiporenko.auth.mapper.UserMapper;
import me.kiporenko.auth.domain.model.User;
import me.kiporenko.auth.repository.UserRepository;
import me.kiporenko.common.dto.PageDTO;
import me.kiporenko.common.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public PageDTO<UserDto> findAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserDto> dtoPage = userPage.map(userMapper::toDto);
        return new PageDTO<>(dtoPage);
    }

    @Transactional
    public UserDto changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        user.setRole(newRole);

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}