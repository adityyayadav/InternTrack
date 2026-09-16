package com.internpilot.applications;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationDto {
    private UUID id;
    private UUID studentId;

    @NotNull(message = "Opportunity ID is required")
    private UUID opportunityId;

    private String status;
    private String coverNote;
    private Instant submittedAt;
    private Instant reviewedAt;
    private UUID reviewedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public static ApplicationDto from(Application app) {
        return ApplicationDto.builder()
                .id(app.getId())
                .studentId(app.getStudent().getId())
                .opportunityId(app.getOpportunity().getId())
                .status(app.getStatus().name())
                .coverNote(app.getCoverNote())
                .submittedAt(app.getSubmittedAt())
                .reviewedAt(app.getReviewedAt())
                .reviewedBy(app.getReviewedBy())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
