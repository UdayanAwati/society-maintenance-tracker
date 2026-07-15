package com.society.maintenance.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "complaint_history")
public class ComplaintHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Complaint complaint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User updatedBy;

    @Column(nullable = false, updatable = false)
    private Instant timestamp = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    public Instant getTimestamp() { return timestamp; }
}
