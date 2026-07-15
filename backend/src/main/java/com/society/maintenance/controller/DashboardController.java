package com.society.maintenance.controller;

import com.society.maintenance.dto.*;
import com.society.maintenance.entity.User;
import com.society.maintenance.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<DashboardDtos.AdminDashboard> admin() {
        return ApiResponse.ok("Admin dashboard", service.admin());
    }
    @GetMapping("/resident")
    ApiResponse<DashboardDtos.ResidentDashboard> resident(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Resident dashboard", service.resident(user));
    }
}
