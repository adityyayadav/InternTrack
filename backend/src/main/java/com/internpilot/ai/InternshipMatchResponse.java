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
public class InternshipMatchResponse {

    private UUID studentId;
    private int totalEvaluated;

    @Builder.Default
    private List<OpportunityMatchDto> matches = new ArrayList<>();
}
