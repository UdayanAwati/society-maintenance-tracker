package com.society.maintenance.mapper;

import com.society.maintenance.dto.*;
import com.society.maintenance.entity.*;

public class AppMapper {
    private AppMapper() {}
    public static AuthDtos.UserResponse user(User user) {
        return new AuthDtos.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                user.getFlatNumber(), user.getProfilePhotoUrl(), user.getRole(), user.getCreatedAt());
    }
    public static ComplaintDtos.HistoryResponse history(ComplaintHistory history) {
        return new ComplaintDtos.HistoryResponse(history.getId(), history.getStatus(), history.getNote(),
                history.getUpdatedBy().getName(), history.getTimestamp());
    }
    public static ComplaintDtos.ComplaintResponse complaint(Complaint complaint) {
        return new ComplaintDtos.ComplaintResponse(complaint.getId(), user(complaint.getResident()),
                complaint.getCategory(), complaint.getDescription(), complaint.getPhotoUrl(), complaint.getAssignedTechnician(),
                complaint.getPriority(), complaint.getStatus(), complaint.getCreatedAt(), complaint.getUpdatedAt(), complaint.getClosedAt(),
                complaint.isOverdue(), complaint.getHistory().stream().map(AppMapper::history).toList());
    }
    public static NoticeDtos.NoticeResponse notice(Notice notice) {
        return new NoticeDtos.NoticeResponse(notice.getId(), notice.getTitle(), notice.getDescription(),
                notice.isImportant(), notice.getCreatedAt(), notice.getCreatedBy().getName());
    }
    public static NotificationDtos.NotificationResponse notification(Notification notification) {
        return new NotificationDtos.NotificationResponse(notification.getId(), notification.getMessage(),
                notification.isRead(), notification.getCreatedAt());
    }
}
