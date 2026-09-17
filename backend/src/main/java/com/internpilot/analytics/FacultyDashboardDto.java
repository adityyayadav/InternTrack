package com.internpilot.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacultyDashboardDto {

    private long assignedStudentsCount;
    private long pendingApplicationReviewsCount;
    private long pendingWeeklyReportReviewsCount;
    private long studentsWithBlockersCount;
    private double overallInternshipProgressPercentage;

    @Builder.Default
    private List<StudentSummaryDto> assignedStudents = new ArrayList<>();

    @Builder.Default
    private List<BlockerAlertDto> activeBlockers = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentSummaryDto {
        private UUID studentId;
        private String fullName;
        private String email;
        private String studentNumber;
        private String internshipTitle;
        private String organization;
        private String applicationStatus;
        private long completedReports;
        private double progressPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BlockerAlertDto {
        private UUID reportId;
        private UUID studentId;
        private String studentName;
        private Integer weekNumber;
        private String blockerDescription;
        private String riskLevel;
        private String status;
    }
}
