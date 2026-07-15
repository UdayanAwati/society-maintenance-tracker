package com.society.maintenance.controller;

import com.society.maintenance.dto.*;
import com.society.maintenance.entity.*;
import com.society.maintenance.service.ComplaintService;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    private final ComplaintService service;
    public ComplaintController(ComplaintService service) { this.service = service; }

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN')")
    ApiResponse<ComplaintDtos.ComplaintResponse> create(@AuthenticationPrincipal User user,
                                                        @Valid @RequestPart("data") ComplaintDtos.CreateComplaintRequest request,
                                                        @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return ApiResponse.ok("Complaint created", service.createResponse(user, request, photo));
    }

    @GetMapping("/mine")
    ApiResponse<Page<ComplaintDtos.ComplaintResponse>> mine(@AuthenticationPrincipal User user, Pageable pageable) {
        return ApiResponse.ok("Complaints", service.residentComplaintResponses(user, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Page<ComplaintDtos.ComplaintResponse>> list(@RequestParam(required = false) ComplaintStatus status,
                                                            @RequestParam(required = false) ComplaintPriority priority,
                                                            @RequestParam(required = false) String category,
                                                            @RequestParam(required = false) Long residentId,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
                                                            @RequestParam(required = false) String search,
                                                            Pageable pageable) {
        return ApiResponse.ok("Complaints", service.searchResponses(status, priority, category, residentId, from, to, search, pageable));
    }

    @GetMapping("/{id}")
    ApiResponse<ComplaintDtos.ComplaintResponse> get(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ApiResponse.ok("Complaint", service.getResponse(id, user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ComplaintDtos.ComplaintResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody ComplaintDtos.UpdateComplaintRequest request,
                                                        @AuthenticationPrincipal User admin) {
        return ApiResponse.ok("Complaint updated", service.updateResponse(id, request, admin));
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.delete(id, user);
        return ApiResponse.ok("Complaint deleted", null);
    }
}
