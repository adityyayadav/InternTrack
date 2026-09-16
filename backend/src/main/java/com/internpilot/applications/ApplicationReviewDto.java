package com.internpilot.applications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReviewDto {
    private ApplicationStatus decision; // APPROVED, REJECTED, CHANGES_REQUESTED
    private String comments;
}
