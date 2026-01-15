package me.kiporenko.auth.controller;

import lombok.RequiredArgsConstructor;
import me.kiporenko.auth.domain.model.Role;
import me.kiporenko.auth.domain.dto.UserDto;
import me.kiporenko.auth.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import me.kiporenko.common.dto.PageDTO;

@RestController
@RequestMapping("admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // Secures all methods in this controller
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<PageDTO<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.findAllUsers(pageable));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<UserDto> grantRole(@PathVariable Long userId, @RequestParam Role role) {
        return ResponseEntity.ok(adminService.changeUserRole(userId, role));
    }
}