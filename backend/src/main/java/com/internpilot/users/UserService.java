package com.internpilot.users;

import com.internpilot.audit.AuditEventService;
import com.internpilot.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ProfileRepository profileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AuditEventService auditEventService;

    /**
     * Get or create a profile from JWT claims.
     * Called on first login to auto-provision the user record.
     */
    @Transactional
    public Profile getOrCreateProfile(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return profileRepository.findById(userId).orElseGet(() -> {
            String email = jwt.getClaimAsString("email");

            // Determine role from JWT metadata
            Role role = Role.STUDENT; // default
            Map<String, Object> appMeta = jwt.getClaimAsMap("app_metadata");
            if (appMeta != null && appMeta.containsKey("role")) {
                try {
                    role = Role.valueOf(appMeta.get("role").toString().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            } else {
                Map<String, Object> userMeta = jwt.getClaimAsMap("user_metadata");
                if (userMeta != null && userMeta.containsKey("role")) {
                    try {
                        role = Role.valueOf(userMeta.get("role").toString().toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }

            // Extract full name from metadata or email
            String fullName = "User";
            Map<String, Object> userMeta = jwt.getClaimAsMap("user_metadata");
            if (userMeta != null) {
                if (userMeta.containsKey("full_name")) {
                    fullName = userMeta.get("full_name").toString();
                } else if (userMeta.containsKey("name")) {
                    fullName = userMeta.get("name").toString();
                }
            }
            if ("User".equals(fullName) && email != null) {
                fullName = email.split("@")[0];
            }

            Profile profile = Profile.builder()
                    .id(userId)
                    .email(email != null ? email : "unknown@unknown.com")
                    .fullName(fullName)
                    .role(role)
                    .build();

            return profileRepository.save(profile);
        });
    }

    public Profile getProfileById(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", id));
    }

    @Transactional
    public StudentProfileDto updateStudentProfile(UUID userId, StudentProfileDto dto) {
        Profile profile = getProfileById(userId);

        StudentProfile sp = studentProfileRepository.findById(userId)
                .orElseGet(() -> StudentProfile.builder().profile(profile).userId(userId).build());

        if (dto.getSkills() != null)
            sp.setSkills(dto.getSkills());
        if (dto.getEducation() != null)
            sp.setEducation(dto.getEducation());
        if (dto.getInterests() != null)
            sp.setInterests(dto.getInterests());
        if (dto.getPreferredDomains() != null)
            sp.setPreferredDomains(dto.getPreferredDomains());
        if (dto.getResumeFileId() != null)
            sp.setResumeFileId(dto.getResumeFileId());

        StudentProfile saved = studentProfileRepository.save(sp);
        auditEventService.record(userId, "STUDENT_PROFILE", userId, "UPDATED", "{}");
        return StudentProfileDto.from(saved);
    }

    public StudentProfileDto getStudentProfile(UUID userId) {
        return studentProfileRepository.findById(userId)
                .map(StudentProfileDto::from)
                .orElse(new StudentProfileDto());
    }
}
