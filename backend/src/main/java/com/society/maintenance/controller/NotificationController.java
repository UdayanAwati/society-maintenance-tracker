package com.society.maintenance.controller;

import com.society.maintenance.dto.*;
import com.society.maintenance.entity.User;
import com.society.maintenance.mapper.AppMapper;
import com.society.maintenance.repository.NotificationRepository;
import com.society.maintenance.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;
    private final NotificationRepository repository;
    public NotificationController(NotificationService service, NotificationRepository repository) {
        this.service = service;
        this.repository = repository;
    }
    @GetMapping
    ApiResponse<List<NotificationDtos.NotificationResponse>> latest(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Notifications", service.latest(user).stream().map(AppMapper::notification).toList());
    }
    @GetMapping("/unread-count")
    ApiResponse<Long> unread(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Unread notifications", service.unread(user));
    }
    @PatchMapping("/{id}/read")
    ApiResponse<Void> read(@PathVariable Long id, @AuthenticationPrincipal User user) {
        repository.findById(id).filter(n -> n.getUser().getId().equals(user.getId())).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
        return ApiResponse.ok("Notification marked read", null);
    }
}
