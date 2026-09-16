package com.internpilot.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentProfileDto {
    private String skills;
    private String education;
    private String interests;
    private String preferredDomains;
    private UUID resumeFileId;

    public static StudentProfileDto from(StudentProfile sp) {
        return StudentProfileDto.builder()
                .skills(sp.getSkills())
                .education(sp.getEducation())
                .interests(sp.getInterests())
                .preferredDomains(sp.getPreferredDomains())
                .resumeFileId(sp.getResumeFileId())
                .build();
    }
}
