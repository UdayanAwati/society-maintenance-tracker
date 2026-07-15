package com.society.maintenance.service;

import com.society.maintenance.dto.AuthDtos;
import com.society.maintenance.entity.Role;
import com.society.maintenance.entity.User;
import com.society.maintenance.exception.ApiException;
import com.society.maintenance.mapper.AppMapper;
import com.society.maintenance.repository.UserRepository;
import com.society.maintenance.security.JwtService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager,
                       JwtService jwtService, EmailService emailService) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (users.existsByEmail(request.email())) throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        var user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(encoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setFlatNumber(request.flatNumber());
        user.setRole(users.count() == 0 ? Role.ADMIN : Role.RESIDENT);
        users.save(user);
        return new AuthDtos.AuthResponse(jwtService.generate(user), AppMapper.user(user));
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = users.findByEmail(request.email()).orElseThrow();
        return new AuthDtos.AuthResponse(jwtService.generate(user), AppMapper.user(user));
    }

    public void forgotPassword(String email, String frontendUrl) {
        users.findByEmail(email).ifPresent(user -> {
            user.setPasswordResetToken(UUID.randomUUID().toString());
            user.setPasswordResetExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
            users.save(user);
            emailService.send(user, "Reset your password",
                    "<p>Use this link to reset your password:</p><p><a href='" + frontendUrl + "/reset-password?token="
                            + user.getPasswordResetToken() + "'>Reset password</a></p>");
        });
    }

    public void resetPassword(AuthDtos.ResetPasswordRequest request) {
        var user = users.findByPasswordResetToken(request.token())
                .filter(u -> u.getPasswordResetExpiresAt() != null && u.getPasswordResetExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token"));
        user.setPassword(encoder.encode(request.password()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        users.save(user);
    }
}
