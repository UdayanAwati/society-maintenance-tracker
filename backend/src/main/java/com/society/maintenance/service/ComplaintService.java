package com.society.maintenance.service;

import com.society.maintenance.dto.ComplaintDtos;
import com.society.maintenance.entity.*;
import com.society.maintenance.exception.ApiException;
import com.society.maintenance.mapper.AppMapper;
import com.society.maintenance.repository.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ComplaintService {
    private final ComplaintRepository complaints;
    private final ComplaintHistoryRepository history;
    private final StorageService storage;
    private final NotificationService notifications;
    private final EmailService emailService;
    private final int overdueDays;

    public ComplaintService(ComplaintRepository complaints, ComplaintHistoryRepository history, StorageService storage,
                            NotificationService notifications, EmailService emailService,
                            @Value("${complaint.overdue.days}") int overdueDays) {
        this.complaints = complaints;
        this.history = history;
        this.storage = storage;
        this.notifications = notifications;
        this.emailService = emailService;
        this.overdueDays = overdueDays;
    }

    @Transactional
    public Complaint create(User resident, ComplaintDtos.CreateComplaintRequest request, MultipartFile photo) {
        var complaint = new Complaint();
        complaint.setResident(resident);
        complaint.setCategory(request.category());
        complaint.setDescription(request.description());
        complaint.setPhotoUrl(storage.store(photo));
        complaints.save(complaint);
        addHistory(complaint, ComplaintStatus.OPEN, "Complaint created", resident);
        notifications.notifyInApp(resident, "Complaint #" + complaint.getId() + " registered successfully");
        emailService.sendComplaintSubmitted(complaint);
        return complaint;
    }

    @Transactional
    public ComplaintDtos.ComplaintResponse createResponse(User resident, ComplaintDtos.CreateComplaintRequest request, MultipartFile photo) {
        return AppMapper.complaint(create(resident, request, photo));
    }

    @Transactional
    public Complaint update(Long id, ComplaintDtos.UpdateComplaintRequest request, User admin) {
        var complaint = get(id);
        ComplaintPriority previousPriority = complaint.getPriority();
        ComplaintStatus previousStatus = complaint.getStatus();
        if (request.priority() != null) complaint.setPriority(request.priority());
        if (request.assignedTechnician() != null) complaint.setAssignedTechnician(blankToNull(request.assignedTechnician()));
        if (request.overdue() != null) complaint.setOverdue(request.overdue());
        ComplaintStatus nextStatus = Boolean.TRUE.equals(request.closeComplaint()) ? ComplaintStatus.RESOLVED : request.status();
        if (request.priority() != null && request.priority() != previousPriority) {
            notifications.notifyInApp(complaint.getResident(),
                    "Complaint #" + complaint.getId() + " priority updated to " + request.priority());
            emailService.sendPriorityAssigned(complaint, request.priority(), admin);
        }
        if (nextStatus != null && nextStatus != complaint.getStatus()) {
            complaint.setStatus(nextStatus);
            if (nextStatus == ComplaintStatus.RESOLVED) complaint.setClosedAt(Instant.now());
            addHistory(complaint, nextStatus, request.note(), admin);
            notifications.notifyInApp(complaint.getResident(),
                    "Complaint #" + complaint.getId() + " status changed to " + nextStatus);
            if (nextStatus == ComplaintStatus.RESOLVED) {
                emailService.sendComplaintResolved(complaint, request.note());
            } else {
                emailService.sendStatusChanged(complaint, previousStatus, nextStatus, request.note(), admin);
            }
        } else if (request.note() != null && !request.note().isBlank()) {
            addHistory(complaint, complaint.getStatus(), request.note(), admin);
        }
        return complaints.save(complaint);
    }

    @Transactional
    public ComplaintDtos.ComplaintResponse updateResponse(Long id, ComplaintDtos.UpdateComplaintRequest request, User admin) {
        return AppMapper.complaint(update(id, request, admin));
    }

    public Complaint get(Long id) {
        return complaints.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Complaint not found"));
    }

    public Page<Complaint> residentComplaints(User resident, Pageable pageable) {
        return complaints.findByResident(resident, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ComplaintDtos.ComplaintResponse> residentComplaintResponses(User resident, Pageable pageable) {
        return complaints.findByResident(resident, pageable).map(AppMapper::complaint);
    }

    public Page<Complaint> search(ComplaintStatus status, ComplaintPriority priority, String category, Long residentId,
                                  Instant from, Instant to, String search, Pageable pageable) {
        return complaints.search(status, priority, blankToNull(category), residentId, from, to, blankToNull(search), pageable);
    }

    @Transactional(readOnly = true)
    public Page<ComplaintDtos.ComplaintResponse> searchResponses(ComplaintStatus status, ComplaintPriority priority,
                                                                 String category, Long residentId, Instant from,
                                                                 Instant to, String search, Pageable pageable) {
        return search(status, priority, category, residentId, from, to, search, pageable).map(AppMapper::complaint);
    }

    @Transactional(readOnly = true)
    public ComplaintDtos.ComplaintResponse getResponse(Long id, User user) {
        var complaint = get(id);
        if (user.getRole() == Role.RESIDENT && !complaint.getResident().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return AppMapper.complaint(complaint);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void markOverdue() {
        var stale = complaints.findByStatusInAndCreatedAtBefore(List.of(ComplaintStatus.OPEN, ComplaintStatus.IN_PROGRESS),
                Instant.now().minus(overdueDays, ChronoUnit.DAYS));
        stale.forEach(complaint -> complaint.setOverdue(true));
        complaints.saveAll(stale);
    }

    private void addHistory(Complaint complaint, ComplaintStatus status, String note, User user) {
        var row = new ComplaintHistory();
        row.setComplaint(complaint);
        row.setStatus(status);
        row.setNote(note);
        row.setUpdatedBy(user);
        complaint.getHistory().add(row);
        history.save(row);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
