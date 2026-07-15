package com.society.maintenance.repository;

import com.society.maintenance.entity.Notification;
import com.society.maintenance.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop20ByUserOrderByCreatedAtDesc(User user);
    long countByUserAndIsReadFalse(User user);
}
