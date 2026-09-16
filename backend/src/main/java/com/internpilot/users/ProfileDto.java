package com.internpilot.users;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ProfileDto {
    private UUID id;
    private String fullName;
    private String email;
    private String role;
    private UUID departmentId;
    private String studentNumber;
    private Instant createdAt;
    private Instant updatedAt;

    public static ProfileDto from(Profile profile) {
        return ProfileDto.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .role(profile.getRole().name())
                .departmentId(profile.getDepartmentId())
                .studentNumber(profile.getStudentNumber())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
