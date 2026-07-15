package com.society.maintenance.dto;

import com.society.maintenance.entity.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;

public class ComplaintDtos {
    public record CreateComplaintRequest(@NotBlank String category, @NotBlank String description) {}
    public record UpdateComplaintRequest(ComplaintStatus status, ComplaintPriority priority, String note,
                                          String assignedTechnician, Boolean overdue, Boolean closeComplaint) {}
    public record ComplaintResponse(Long id, AuthDtos.UserResponse resident, String category, String description,
                                    String photoUrl, String assignedTechnician, ComplaintPriority priority, ComplaintStatus status,
                                    Instant createdAt, Instant updatedAt, Instant closedAt, boolean overdue,
                                    List<HistoryResponse> history) {}
    public record HistoryResponse(Long id, ComplaintStatus status, String note, String updatedBy, Instant timestamp) {}
}
