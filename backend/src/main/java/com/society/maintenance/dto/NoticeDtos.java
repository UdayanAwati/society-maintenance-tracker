package com.society.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class NoticeDtos {
    public record NoticeRequest(@NotBlank String title, @NotBlank String description, boolean important) {}
    public record NoticeResponse(Long id, String title, String description, boolean important,
                                 Instant createdAt, String createdBy) {}
}
