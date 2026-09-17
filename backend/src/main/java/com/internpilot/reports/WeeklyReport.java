package com.internpilot.reports;

import com.internpilot.applications.Application;
import com.internpilot.users.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "weekly_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Profile student;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "tasks_completed", nullable = false, columnDefinition = "text")
    private String tasksCompleted;

    @Column(name = "skills_learned", columnDefinition = "jsonb")
    private String skillsLearned;

    @Column(name = "challenges_and_blockers", columnDefinition = "text")
    private String challengesAndBlockers;

    @Column(name = "next_week_plan", columnDefinition = "text")
    private String nextWeekPlan;

    @Builder.Default
    @Column(name = "hours_worked", nullable = false)
    private Integer hoursWorked = 0;

    @Column(name = "attachment_file_id")
    private UUID attachmentFileId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.DRAFT;

    @Column(name = "faculty_feedback", columnDefinition = "text")
    private String facultyFeedback;

    @Column(name = "faculty_rating")
    private Integer facultyRating;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "ai_summary", columnDefinition = "text")
    private String aiSummary;

    @Column(name = "ai_risk_flag")
    private String aiRiskFlag;

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
