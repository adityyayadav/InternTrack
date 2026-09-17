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
public class OpportunityMatchDto {

    private UUID opportunityId;
    private String title;
    private String organization;
    private String location;
    private String workMode;
    private int matchPercentage;
    private String matchLevel; // HIGH, MEDIUM, LOW
    private String matchReason;

    @Builder.Default
    private List<String> matchedSkills = new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();
}
