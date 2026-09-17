package com.internpilot.ai;

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
public class ResumeAnalysisResponse {

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Builder.Default
    private List<String> technologies = new ArrayList<>();

    @Builder.Default
    private List<EducationEntry> education = new ArrayList<>();

    @Builder.Default
    private List<ExperienceEntry> experience = new ArrayList<>();

    private String summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EducationEntry {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private String graduationYear;
        private String gpa;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExperienceEntry {
        private String organization;
        private String role;
        private String duration;
        private String description;
    }
}
