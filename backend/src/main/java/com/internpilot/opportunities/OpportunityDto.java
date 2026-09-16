package com.internpilot.opportunities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
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
public class OpportunityDto {
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Organization is required")
    private String organization;

    @NotBlank(message = "Description is required")
    private String description;

    private String requiredSkills;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Work mode is required")
    private String workMode;

    private String duration;
    private Instant applicationDeadline;
    private Boolean isActive;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    public static OpportunityDto from(Opportunity opportunity) {
        return OpportunityDto.builder()
                .id(opportunity.getId())
                .title(opportunity.getTitle())
                .organization(opportunity.getOrganization())
                .description(opportunity.getDescription())
                .requiredSkills(opportunity.getRequiredSkills())
                .location(opportunity.getLocation())
                .workMode(opportunity.getWorkMode())
                .duration(opportunity.getDuration())
                .applicationDeadline(opportunity.getApplicationDeadline())
                .isActive(opportunity.isActive())
                .createdBy(opportunity.getCreatedBy())
                .createdAt(opportunity.getCreatedAt())
                .updatedAt(opportunity.getUpdatedAt())
                .build();
    }
}
