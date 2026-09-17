package com.internpilot.reports;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateWeeklyReportDto {

    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    @NotNull(message = "Week number is required")
    @Min(value = 1, message = "Week number must be at least 1")
    private Integer weekNumber;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Tasks completed is required")
    private String tasksCompleted;

    private String skillsLearned;

    private String challengesAndBlockers;

    private String nextWeekPlan;

    @NotNull(message = "Hours worked is required")
    @Min(value = 0, message = "Hours worked cannot be negative")
    private Integer hoursWorked;

    private UUID attachmentFileId;
}
