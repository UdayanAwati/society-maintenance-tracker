package com.society.maintenance.service;

import com.society.maintenance.entity.*;
import com.society.maintenance.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notifications, EmailService emailService) {
        this.notifications = notifications;
        this.emailService = emailService;
    }
    public void notify(User user, String message, String emailSubject) {
        var notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notifications.save(notification);
        emailService.send(user, emailSubject, "<div style='font-family:Arial'><h2>Society Maintenance Tracker</h2><p>" + message + "</p></div>");
    }
    public void notifyInApp(User user, String message) {
        var notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notifications.save(notification);
    }
    public List<Notification> latest(User user) {
        return notifications.findTop20ByUserOrderByCreatedAtDesc(user);
    }
    public long unread(User user) {
        return notifications.countByUserAndIsReadFalse(user);
    }
}
