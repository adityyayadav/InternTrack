package com.internpilot.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDashboardDto {

    private long totalStudents;
    private long totalFaculty;
    private long activeOpportunities;
    private long totalApplications;
    private long activeInternships;

    private PlacementStatsDto placementStats;

    @Builder.Default
    private List<DepartmentMetricDto> departmentInternships = new ArrayList<>();

    @Builder.Default
    private List<MonthlyTrendDto> monthlyTrends = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PlacementStatsDto {
        private long placedStudents;
        private long seekingStudents;
        private double placementRatePercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DepartmentMetricDto {
        private String departmentName;
        private String departmentCode;
        private long internshipCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MonthlyTrendDto {
        private String month;
        private long applicationCount;
    }
}
