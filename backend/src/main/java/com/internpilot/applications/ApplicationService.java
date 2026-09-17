package com.internpilot.applications;

import com.internpilot.common.exception.ResourceNotFoundException;
import com.internpilot.opportunities.Opportunity;
import com.internpilot.opportunities.OpportunityRepository;
import com.internpilot.notifications.NotificationService;
import com.internpilot.notifications.NotificationType;
import com.internpilot.users.FacultyAssignment;
import com.internpilot.users.FacultyAssignmentRepository;
import com.internpilot.users.Profile;
import com.internpilot.users.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final ProfileRepository profileRepository;
    private final ApplicationReviewRepository reviewRepository;
    private final NotificationService notificationService;
    private final FacultyAssignmentRepository facultyAssignmentRepository;

    public Page<ApplicationDto> getStudentApplications(UUID studentId, Pageable pageable) {
        return applicationRepository.findByStudentId(studentId, pageable)
                .map(ApplicationDto::from);
    }

    public ApplicationDto getApplicationById(UUID id, UUID studentId) {
        Application app = applicationRepository.findByIdAndStudentId(id, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));
        return ApplicationDto.from(app);
    }

    @Transactional
    public ApplicationDto createDraft(ApplicationDto dto, UUID studentId) {
        if (applicationRepository.existsByStudentIdAndOpportunityId(studentId, dto.getOpportunityId())) {
            throw new IllegalStateException("You already have an application for this opportunity.");
        }

        Profile student = profileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", studentId));
        Opportunity opp = opportunityRepository.findById(dto.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", dto.getOpportunityId()));

        Application app = Application.builder()
                .student(student)
                .opportunity(opp)
                .status(ApplicationStatus.DRAFT)
                .coverNote(dto.getCoverNote())
                .build();

        return ApplicationDto.from(applicationRepository.save(app));
    }

    @Transactional
    public ApplicationDto submitApplication(UUID id, UUID studentId, ApplicationDto dto) {
        Application app = applicationRepository.findByIdAndStudentId(id, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (app.getStatus() != ApplicationStatus.DRAFT && app.getStatus() != ApplicationStatus.CHANGES_REQUESTED) {
            throw new IllegalStateException("Can only submit applications in DRAFT or CHANGES_REQUESTED state.");
        }

        if (dto.getCoverNote() != null) {
            app.setCoverNote(dto.getCoverNote());
        }

        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setSubmittedAt(Instant.now());

        return ApplicationDto.from(applicationRepository.save(app));
    }

    @Transactional
    public ApplicationDto reviewApplication(UUID id, UUID facultyId, ApplicationReviewDto dto) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (app.getStatus() != ApplicationStatus.SUBMITTED && app.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Can only review applications that are submitted or under review.");
        }

        // Create the review record
        ApplicationReview review = ApplicationReview.builder()
                .applicationId(app.getId())
                .reviewerId(facultyId)
                .decision(dto.getDecision())
                .comments(dto.getComments())
                .build();
        reviewRepository.save(review);

        // Update application state
        app.setStatus(dto.getDecision());
        app.setReviewedAt(Instant.now());
        app.setReviewedBy(facultyId);

        return ApplicationDto.from(applicationRepository.save(app));
    }
}
