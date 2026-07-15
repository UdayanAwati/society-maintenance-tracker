package com.society.maintenance.dto;

import java.util.List;

public class DashboardDtos {
    public record Metric(String label, long value) {}
    public record AdminDashboard(long totalComplaints, long open, long inProgress, long resolved, long overdue,
                                 List<Metric> byStatus, List<Metric> byCategory, List<Metric> byPriority,
                                 Object recentComplaints, Object recentNotices) {}
    public record ResidentDashboard(long totalComplaints, long open, long resolved, long pending,
                                    Object recentComplaints, Object recentNotices) {}
}
