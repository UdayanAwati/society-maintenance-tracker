package com.society.maintenance.controller;

import com.society.maintenance.dto.*;
import com.society.maintenance.entity.User;
import com.society.maintenance.mapper.AppMapper;
import com.society.maintenance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final String frontendUrl;
    public AuthController(AuthService authService, @Value("${app.frontend-url}") String frontendUrl) {
        this.authService = authService;
        this.frontendUrl = frontendUrl;
    }
    @PostMapping("/register")
    ApiResponse<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ApiResponse.ok("Registered successfully", authService.register(request));
    }
    @PostMapping("/login")
    ApiResponse<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.ok("Logged in successfully", authService.login(request));
    }
    @PostMapping("/logout")
    ApiResponse<Void> logout() {
        return ApiResponse.ok("Logged out successfully", null);
    }
    @GetMapping("/me")
    ApiResponse<AuthDtos.UserResponse> me(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Current user", AppMapper.user(user));
    }
    @PostMapping("/forgot-password")
    ApiResponse<Void> forgot(@Valid @RequestBody AuthDtos.ForgotPasswordRequest request) {
        authService.forgotPassword(request.email(), frontendUrl);
        return ApiResponse.ok("Password reset instructions sent if the email exists", null);
    }
    @PostMapping("/reset-password")
    ApiResponse<Void> reset(@Valid @RequestBody AuthDtos.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("Password reset successfully", null);
    }
}
