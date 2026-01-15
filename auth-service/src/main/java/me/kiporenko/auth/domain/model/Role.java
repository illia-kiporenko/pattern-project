package me.kiporenko.auth.domain.model;

public enum Role {
    USER,       // Basic user
    MODERATOR,  // Can manage content (e.g., delete posts)
    ADMIN       // Can manage users and system settings
}