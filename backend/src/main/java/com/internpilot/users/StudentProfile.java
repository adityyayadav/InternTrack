package com.internpilot.users;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Profile profile;

    @Column(columnDefinition = "jsonb")
    private String skills;

    @Column(columnDefinition = "text")
    private String education;

    @Column(columnDefinition = "text")
    private String interests;

    @Column(name = "preferred_domains", columnDefinition = "text")
    private String preferredDomains;

    @Column(name = "resume_file_id")
    private UUID resumeFileId;

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
