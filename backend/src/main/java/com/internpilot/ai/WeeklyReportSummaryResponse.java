package com.internpilot.ai;

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
public class WeeklyReportSummaryResponse {

    private UUID reportId;
    private Integer weekNumber;
    private String studentName;
    private String executiveSummary;

    @Builder.Default
    private List<String> keyAchievements = new ArrayList<>();

    @Builder.Default
    private List<String> blockersIdentified = new ArrayList<>();

    private String riskAssessment; // LOW, MEDIUM, HIGH

    @Builder.Default
    private List<String> recommendedActions = new ArrayList<>();
}
