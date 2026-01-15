package me.kiporenko.auth.domain.dto;

import lombok.Builder;
import lombok.Data;
import me.kiporenko.auth.domain.model.Role;

import java.util.List;

// This DTO represents a user's public profile.
// Note the absence of the password field.
@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    // We will add more profile fields here later
    private String firstName;
    private String lastName;
    private String bio;
}