package com.society.maintenance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Entity
@Table(name = "notices")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;
    private boolean important;
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User createdBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isImportant() { return important; }
    public void setImportant(boolean important) { this.important = important; }
    public Instant getCreatedAt() { return createdAt; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
