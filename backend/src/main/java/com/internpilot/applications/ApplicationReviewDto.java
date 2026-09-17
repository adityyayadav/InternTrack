package com.internpilot.applications;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReviewDto {
    @NotNull(message = "Decision is required")
    private ApplicationStatus decision;
    @Size(max = 2000, message = "Comments must be at most 2000 characters")
    private String comments;
}
