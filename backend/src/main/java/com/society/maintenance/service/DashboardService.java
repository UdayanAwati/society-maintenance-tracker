package com.society.maintenance.service;

import com.society.maintenance.dto.DashboardDtos;
import com.society.maintenance.entity.*;
import com.society.maintenance.mapper.AppMapper;
import com.society.maintenance.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final ComplaintRepository complaints;
    private final NoticeRepository notices;

    public DashboardService(ComplaintRepository complaints, NoticeRepository notices) {
        this.complaints = complaints;
        this.notices = notices;
    }
    @Transactional(readOnly = true)
    public DashboardDtos.AdminDashboard admin() {
        var recentComplaints = complaints.findAll(PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "isOverdue", "createdAt")))
                .map(AppMapper::complaint).getContent();
        var recentNotices = notices.findAllByOrderByImportantDescCreatedAtDesc(PageRequest.of(0, 5))
                .map(AppMapper::notice).getContent();
        return new DashboardDtos.AdminDashboard(complaints.count(), complaints.countByStatus(ComplaintStatus.OPEN),
                complaints.countByStatus(ComplaintStatus.IN_PROGRESS), complaints.countByStatus(ComplaintStatus.RESOLVED),
                complaints.countByIsOverdueTrue(),
                complaints.countByStatusMetric().stream().map(r -> new DashboardDtos.Metric(String.valueOf(r.getLabel()), r.getValue())).toList(),
                complaints.countByCategoryMetric().stream().map(r -> new DashboardDtos.Metric(String.valueOf(r.getLabel()), r.getValue())).toList(),
                complaints.countByPriorityMetric().stream().map(r -> new DashboardDtos.Metric(String.valueOf(r.getLabel()), r.getValue())).toList(),
                recentComplaints, recentNotices);
    }
    @Transactional(readOnly = true)
    public DashboardDtos.ResidentDashboard resident(User user) {
        var recentComplaints = complaints.findByResident(user, PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(AppMapper::complaint).getContent();
        var recentNotices = notices.findAllByOrderByImportantDescCreatedAtDesc(PageRequest.of(0, 5))
                .map(AppMapper::notice).getContent();
        long open = complaints.countByResidentAndStatus(user, ComplaintStatus.OPEN);
        long resolved = complaints.countByResidentAndStatus(user, ComplaintStatus.RESOLVED);
        return new DashboardDtos.ResidentDashboard(complaints.countByResident(user), open, resolved,
                complaints.countByResidentAndStatus(user, ComplaintStatus.IN_PROGRESS) + open, recentComplaints, recentNotices);
    }
}
