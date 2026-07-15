package com.society.maintenance.service;

import com.society.maintenance.dto.NoticeDtos;
import com.society.maintenance.entity.*;
import com.society.maintenance.exception.ApiException;
import com.society.maintenance.mapper.AppMapper;
import com.society.maintenance.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeService {
    private final NoticeRepository notices;
    private final UserRepository users;
    private final NotificationService notifications;
    private final EmailService emailService;

    public NoticeService(NoticeRepository notices, UserRepository users, NotificationService notifications,
                         EmailService emailService) {
        this.notices = notices;
        this.users = users;
        this.notifications = notifications;
        this.emailService = emailService;
    }
    public Page<Notice> list(Pageable pageable) {
        return notices.findAllByOrderByImportantDescCreatedAtDesc(pageable);
    }
    @Transactional(readOnly = true)
    public Page<NoticeDtos.NoticeResponse> listResponses(Pageable pageable) {
        return list(pageable).map(AppMapper::notice);
    }
    @Transactional
    public Notice create(NoticeDtos.NoticeRequest request, User admin) {
        var notice = new Notice();
        apply(notice, request);
        notice.setCreatedBy(admin);
        notices.save(notice);
        if (notice.isImportant()) {
            users.findByRole(Role.RESIDENT).forEach(user -> {
                notifications.notifyInApp(user, "Important notice posted: " + notice.getTitle());
                emailService.sendImportantNotice(user, notice);
            });
        }
        return notice;
    }
    @Transactional
    public NoticeDtos.NoticeResponse createResponse(NoticeDtos.NoticeRequest request, User admin) {
        return AppMapper.notice(create(request, admin));
    }
    @Transactional
    public Notice update(Long id, NoticeDtos.NoticeRequest request) {
        var notice = get(id);
        boolean becameImportant = !notice.isImportant() && request.important();
        apply(notice, request);
        notices.save(notice);
        if (becameImportant) {
            users.findByRole(Role.RESIDENT).forEach(user -> {
                notifications.notifyInApp(user, "Important notice posted: " + notice.getTitle());
                emailService.sendImportantNotice(user, notice);
            });
        }
        return notice;
    }
    @Transactional
    public NoticeDtos.NoticeResponse updateResponse(Long id, NoticeDtos.NoticeRequest request) {
        return AppMapper.notice(update(id, request));
    }
    public void delete(Long id) {
        notices.delete(get(id));
    }
    public Notice get(Long id) {
        return notices.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notice not found"));
    }
    private void apply(Notice notice, NoticeDtos.NoticeRequest request) {
        notice.setTitle(request.title());
        notice.setDescription(request.description());
        notice.setImportant(request.important());
    }
}
