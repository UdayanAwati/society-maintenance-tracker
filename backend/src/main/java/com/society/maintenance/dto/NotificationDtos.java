package com.society.maintenance.dto;

import java.time.Instant;

public class NotificationDtos {
    public record NotificationResponse(Long id, String message, boolean read, Instant createdAt) {}
}
