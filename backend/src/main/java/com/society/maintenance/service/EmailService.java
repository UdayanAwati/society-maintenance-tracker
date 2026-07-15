package com.society.maintenance.service;

import com.society.maintenance.entity.Complaint;
import com.society.maintenance.entity.ComplaintPriority;
import com.society.maintenance.entity.ComplaintStatus;
import com.society.maintenance.entity.Notice;
import com.society.maintenance.entity.User;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
            .withZone(ZoneId.systemDefault());

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;
    private final String frontendUrl;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.enabled}") boolean enabled,
                        @Value("${spring.mail.username}") String from,
                        @Value("${app.frontend-url}") String frontendUrl) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    public void send(User user, String subject, String html) {
        if (!enabled || from == null || from.isBlank()) {
            return;
        }
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception ignored) {
            // Email failure must not break core complaint workflows.
        }
    }

    public void sendComplaintSubmitted(Complaint complaint) {
        User resident = complaint.getResident();
        send(resident, "Complaint Registered Successfully - #" + complaint.getId(), layout("Complaint Registered Successfully",
                row("Resident Name", resident.getName()) +
                row("Complaint ID", "#" + complaint.getId()) +
                row("Category", complaint.getCategory()) +
                row("Description", complaint.getDescription()) +
                row("Date & Time", format(complaint.getCreatedAt())) +
                row("Current Status", "OPEN") +
                paragraph("Thank you for submitting your complaint. Our society maintenance team will review it and keep you updated.")));
    }

    public void sendPriorityAssigned(Complaint complaint, ComplaintPriority priority, User admin) {
        send(complaint.getResident(), "Priority Updated - Complaint #" + complaint.getId(), layout("Priority Updated",
                row("Complaint ID", "#" + complaint.getId()) +
                row("Priority", priority.name()) +
                row("Admin Name", admin.getName()) +
                row("Updated Time", format(Instant.now()))));
    }

    public void sendStatusChanged(Complaint complaint, ComplaintStatus previousStatus, ComplaintStatus newStatus,
                                  String adminNote, User admin) {
        send(complaint.getResident(), "Complaint Status Updated - #" + complaint.getId(), layout("Complaint Status Updated",
                row("Resident Name", complaint.getResident().getName()) +
                row("Complaint ID", "#" + complaint.getId()) +
                row("Previous Status", previousStatus.name()) +
                row("New Status", newStatus.name()) +
                row("Admin Note", blank(adminNote)) +
                row("Updated By", admin.getName()) +
                row("Updated Time", format(Instant.now()))));
    }

    public void sendComplaintResolved(Complaint complaint, String adminNote) {
        send(complaint.getResident(), "Complaint Resolved Successfully", layout("Complaint Resolved Successfully",
                row("Complaint ID", "#" + complaint.getId()) +
                row("Category", complaint.getCategory()) +
                row("Resolution Date", format(complaint.getClosedAt() == null ? Instant.now() : complaint.getClosedAt())) +
                row("Admin Note", blank(adminNote)) +
                paragraph("Thank you for your patience. Your complaint has been marked as resolved.") +
                button("View Complaint", frontendUrl + "/complaints/" + complaint.getId())));
    }

    public void sendImportantNotice(User resident, Notice notice) {
        send(resident, "Important Society Notice", layout("Important Society Notice",
                highlight(escape(notice.getTitle())) +
                row("Notice Title", notice.getTitle()) +
                row("Description", notice.getDescription()) +
                row("Date", format(notice.getCreatedAt())) +
                row("Posted By", notice.getCreatedBy().getName()) +
                button("View Notice", frontendUrl + "/notices")));
    }

    private String layout(String title, String body) {
        return """
                <div style="margin:0;background:#f4f7fb;padding:24px;font-family:Arial,sans-serif;color:#172033">
                  <div style="max-width:640px;margin:0 auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:10px;overflow:hidden">
                    <div style="background:#2563eb;color:#ffffff;padding:18px 22px">
                      <h1 style="margin:0;font-size:20px">Society Maintenance Tracker</h1>
                    </div>
                    <div style="padding:22px">
                      <h2 style="margin:0 0 16px;font-size:22px;color:#111827">%s</h2>
                      %s
                    </div>
                  </div>
                </div>
                """.formatted(escape(title), body);
    }

    private String row(String label, String value) {
        return """
                <div style="padding:10px 0;border-bottom:1px solid #edf2f7">
                  <strong style="display:inline-block;width:160px;color:#475569">%s</strong>
                  <span>%s</span>
                </div>
                """.formatted(escape(label), escape(value));
    }

    private String paragraph(String value) {
        return "<p style=\"line-height:1.6;color:#334155\">" + escape(value) + "</p>";
    }

    private String highlight(String value) {
        return "<div style=\"border-left:4px solid #dc2626;background:#fff1f2;padding:14px 16px;margin-bottom:16px;font-weight:700;color:#991b1b\">" + value + "</div>";
    }

    private String button(String label, String href) {
        return "<p style=\"margin-top:22px\"><a href=\"" + escape(href) + "\" style=\"background:#2563eb;color:#ffffff;text-decoration:none;padding:11px 16px;border-radius:6px;display:inline-block;font-weight:700\">" + escape(label) + "</a></p>";
    }

    private String format(Instant instant) {
        return FORMATTER.format(instant == null ? Instant.now() : instant);
    }

    private String blank(String value) {
        return value == null || value.isBlank() ? "No note added" : value;
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
