package com.miniproject.qpgenerator.auth.service;

import com.miniproject.qpgenerator.auth.dto.UserRequest;
import com.miniproject.qpgenerator.auth.dto.UserResponse;
import com.miniproject.qpgenerator.auth.repository.UserRepository;
import com.miniproject.qpgenerator.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     */
    public UserResponse createUser(UserRequest request) {
        log.info("Creating new user: {}", request.getUsername());

        // Check if user already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : "FACULTY")
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return mapToResponse(user);
    }

    /**
     * Get user by username
     */
    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return mapToResponse(user);
    }

    /**
     * Get all users
     */
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all faculty users
     */
    public List<UserResponse> getAllFaculty() {
        log.info("Fetching all faculty users");
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().equals("FACULTY"))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all admin users
     */
    public List<UserResponse> getAllAdmins() {
        log.info("Fetching all admin users");
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().equals("ADMIN"))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user
     */
    public UserResponse updateUser(Long userId, UserRequest request) {
        log.info("Updating user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : user.getRole());

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", userId);

        return mapToResponse(updatedUser);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * Disable/Enable user
     */
    public UserResponse setUserStatus(Long userId, boolean enabled) {
        log.info("Setting user status: {}, enabled: {}", userId, enabled);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long userId) {
        log.info("Deleting user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }

        userRepository.deleteById(userId);
        log.info("User deleted successfully: {}", userId);
    }

    /**
     * Map User entity to UserResponse DTO
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }
}
