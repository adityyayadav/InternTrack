package com.internpilot.opportunities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "internship_opportunities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String organization;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "required_skills", columnDefinition = "jsonb")
    private String requiredSkills; // Mapped as String for JSONB initially for simplicity

    @Column(nullable = false)
    private String location;

    @Column(name = "work_mode", nullable = false)
    private String workMode; // REMOTE, ONSITE, HYBRID

    private String duration;

    @Column(name = "application_deadline")
    private Instant applicationDeadline;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
