package com.society.maintenance.controller;

import com.society.maintenance.dto.*;
import com.society.maintenance.entity.User;
import com.society.maintenance.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeService service;
    public NoticeController(NoticeService service) { this.service = service; }
    @GetMapping
    ApiResponse<Page<NoticeDtos.NoticeResponse>> list(Pageable pageable) {
        return ApiResponse.ok("Notices", service.listResponses(pageable));
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<NoticeDtos.NoticeResponse> create(@Valid @RequestBody NoticeDtos.NoticeRequest request,
                                                  @AuthenticationPrincipal User admin) {
        return ApiResponse.ok("Notice created", service.createResponse(request, admin));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<NoticeDtos.NoticeResponse> update(@PathVariable Long id, @Valid @RequestBody NoticeDtos.NoticeRequest request) {
        return ApiResponse.ok("Notice updated", service.updateResponse(id, request));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok("Notice deleted", null);
    }
}
