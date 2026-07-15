package com.society.maintenance.dto;

import com.society.maintenance.entity.Role;
import jakarta.validation.constraints.*;
import java.time.Instant;

public class AuthDtos {
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email,
                                  @Size(min = 8) String password, String phone,
                                  @NotBlank String flatNumber) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record ForgotPasswordRequest(@Email @NotBlank String email) {}
    public record ResetPasswordRequest(@NotBlank String token, @Size(min = 8) String password) {}
    public record UpdateProfileRequest(@NotBlank String name, @Email @NotBlank String email,
                                       String phone, @NotBlank String flatNumber) {}
    public record ChangePasswordRequest(@NotBlank String currentPassword, @Size(min = 8) String newPassword) {}
    public record AuthResponse(String token, UserResponse user) {}
    public record UserResponse(Long id, String name, String email, String phone, String flatNumber,
                               String profilePhotoUrl, Role role, Instant createdAt) {}
}
