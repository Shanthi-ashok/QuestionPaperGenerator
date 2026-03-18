package com.miniproject.qpgenerator.auth.controller;

import com.miniproject.qpgenerator.auth.dto.UserRequest;
import com.miniproject.qpgenerator.auth.dto.UserResponse;
import com.miniproject.qpgenerator.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user (ADMIN only)
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all users (ADMIN only)
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all faculty users
     */
    @GetMapping("/role/FACULTY")
    public ResponseEntity<List<UserResponse>> getAllFaculty() {
        List<UserResponse> response = userService.getAllFaculty();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all admin users
     */
    @GetMapping("/role/ADMIN")
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        List<UserResponse> response = userService.getAllAdmins();
        return ResponseEntity.ok(response);
    }

    /**
     * Update user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Change user password
     */
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    /**
     * Disable user
     */
    @PostMapping("/{userId}/disable")
    public ResponseEntity<UserResponse> disableUser(@PathVariable Long userId) {
        UserResponse response = userService.setUserStatus(userId, false);
        return ResponseEntity.ok(response);
    }

    /**
     * Enable user
     */
    @PostMapping("/{userId}/enable")
    public ResponseEntity<UserResponse> enableUser(@PathVariable Long userId) {
        UserResponse response = userService.setUserStatus(userId, true);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
