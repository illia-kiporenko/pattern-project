package me.kiporenko.auth.service;

import lombok.RequiredArgsConstructor;
import me.kiporenko.auth.domain.dto.ChangePasswordRequest;
import me.kiporenko.auth.domain.dto.UpdateProfileRequest;
import me.kiporenko.auth.domain.dto.UserDto;
import me.kiporenko.auth.mapper.UserMapper;
import me.kiporenko.auth.domain.model.User;
import me.kiporenko.auth.repository.UserRepository;
import me.kiporenko.common.exception.EntityNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changeCurrentUserPassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", 0L));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", 0L)); // Or a more specific exception
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateCurrentUserProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", 0L));

        // Update fields if they are provided in the request
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}