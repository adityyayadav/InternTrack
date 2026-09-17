package com.internpilot.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeeklyReportDto {

    private UUID id;
    private UUID applicationId;
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private String opportunityTitle;
    private String organization;

    private Integer weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String tasksCompleted;
    private String skillsLearned;
    private String challengesAndBlockers;
    private String nextWeekPlan;
    private Integer hoursWorked;
    private UUID attachmentFileId;

    private String status;
    private String facultyFeedback;
    private Integer facultyRating;
    private UUID reviewedBy;
    private Instant reviewedAt;
    private Instant submittedAt;

    private String aiSummary;
    private String aiRiskFlag;

    private Instant createdAt;
    private Instant updatedAt;

    public static WeeklyReportDto from(WeeklyReport report) {
        String oppTitle = null;
        String org = null;
        if (report.getApplication() != null && report.getApplication().getOpportunity() != null) {
            oppTitle = report.getApplication().getOpportunity().getTitle();
            org = report.getApplication().getOpportunity().getOrganization();
        }

        String studentName = null;
        String studentEmail = null;
        if (report.getStudent() != null) {
            studentName = report.getStudent().getFullName();
            studentEmail = report.getStudent().getEmail();
        }

        return WeeklyReportDto.builder()
                .id(report.getId())
                .applicationId(report.getApplication() != null ? report.getApplication().getId() : null)
                .studentId(report.getStudent() != null ? report.getStudent().getId() : null)
                .studentName(studentName)
                .studentEmail(studentEmail)
                .opportunityTitle(oppTitle)
                .organization(org)
                .weekNumber(report.getWeekNumber())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .tasksCompleted(report.getTasksCompleted())
                .skillsLearned(report.getSkillsLearned())
                .challengesAndBlockers(report.getChallengesAndBlockers())
                .nextWeekPlan(report.getNextWeekPlan())
                .hoursWorked(report.getHoursWorked())
                .attachmentFileId(report.getAttachmentFileId())
                .status(report.getStatus() != null ? report.getStatus().name() : null)
                .facultyFeedback(report.getFacultyFeedback())
                .facultyRating(report.getFacultyRating())
                .reviewedBy(report.getReviewedBy())
                .reviewedAt(report.getReviewedAt())
                .submittedAt(report.getSubmittedAt())
                .aiSummary(report.getAiSummary())
                .aiRiskFlag(report.getAiRiskFlag())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
