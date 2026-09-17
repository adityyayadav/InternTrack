package com.internpilot.reports;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWeeklyReportDto {

    @Min(value = 1, message = "Week number must be at least 1")
    private Integer weekNumber;

    private LocalDate startDate;

    private LocalDate endDate;

    private String tasksCompleted;

    private String skillsLearned;

    private String challengesAndBlockers;

    private String nextWeekPlan;

    @Min(value = 0, message = "Hours worked cannot be negative")
    private Integer hoursWorked;

    private UUID attachmentFileId;
}
