package com.society.maintenance.controller;

import com.society.maintenance.dto.*;
import com.society.maintenance.entity.User;
import com.society.maintenance.mapper.AppMapper;
import com.society.maintenance.repository.UserRepository;
import com.society.maintenance.service.StorageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    public UserController(UserRepository users, PasswordEncoder passwordEncoder, StorageService storageService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Page<AuthDtos.UserResponse>> list(Pageable pageable) {
        return ApiResponse.ok("Users", users.findAll(pageable).map(AppMapper::user));
    }

    @PatchMapping("/me")
    ApiResponse<AuthDtos.UserResponse> updateMe(@AuthenticationPrincipal User user,
                                                @Valid @RequestBody AuthDtos.UpdateProfileRequest request) {
        users.findByEmail(request.email().toLowerCase())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new com.society.maintenance.exception.ApiException(org.springframework.http.HttpStatus.CONFLICT, "Email already registered");
                });
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPhone(request.phone());
        user.setFlatNumber(request.flatNumber());
        users.save(user);
        return ApiResponse.ok("Profile updated", AppMapper.user(user));
    }

    @PostMapping("/me/photo")
    ApiResponse<AuthDtos.UserResponse> updatePhoto(@AuthenticationPrincipal User user,
                                                   @RequestPart("photo") MultipartFile photo) {
        user.setProfilePhotoUrl(storageService.store(photo));
        users.save(user);
        return ApiResponse.ok("Profile photo updated", AppMapper.user(user));
    }

    @PatchMapping("/me/password")
    ApiResponse<Void> changePassword(@AuthenticationPrincipal User user,
                                     @Valid @RequestBody AuthDtos.ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new com.society.maintenance.exception.ApiException(org.springframework.http.HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        users.save(user);
        return ApiResponse.ok("Password changed", null);
    }
}
