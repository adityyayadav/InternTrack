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
public class StudentDashboardDto {

    private long totalApplications;
    private long acceptedCount;
    private long pendingCount;
    private long rejectedCount;
    private long draftCount;

    private double weeklyReportCompletionPercentage;
    private long totalWeeklyReports;
    private long approvedWeeklyReports;

    @Builder.Default
    private List<UpcomingInternshipDto> activeInternships = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpcomingInternshipDto {
        private UUID applicationId;
        private UUID opportunityId;
        private String opportunityTitle;
        private String organization;
        private String location;
        private String workMode;
        private String duration;
        private String status;
    }
}
