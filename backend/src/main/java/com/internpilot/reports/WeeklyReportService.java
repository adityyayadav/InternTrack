package com.internpilot.reports;

import com.internpilot.applications.Application;
import com.internpilot.applications.ApplicationRepository;
import com.internpilot.applications.ApplicationStatus;
import com.internpilot.common.exception.ResourceNotFoundException;
import com.internpilot.users.FacultyAssignmentRepository;
import com.internpilot.users.Profile;
import com.internpilot.users.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final ApplicationRepository applicationRepository;
    private final ProfileRepository profileRepository;
    private final FacultyAssignmentRepository facultyAssignmentRepository;

    public Page<WeeklyReportDto> getStudentReports(UUID studentId, Pageable pageable) {
        return weeklyReportRepository.findByStudentId(studentId, pageable)
                .map(WeeklyReportDto::from);
    }

    public WeeklyReportDto getReportById(UUID id, UUID requesterId, boolean isFacultyOrAdmin) {
        WeeklyReport report = weeklyReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport", id));

        if (!isFacultyOrAdmin && !report.getStudent().getId().equals(requesterId)) {
            throw new AccessDeniedException("You do not have permission to view this report.");
        }

        return WeeklyReportDto.from(report);
    }

    public Page<WeeklyReportDto> getReportsByApplication(UUID applicationId, UUID requesterId, boolean isFacultyOrAdmin, Pageable pageable) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (!isFacultyOrAdmin && !application.getStudent().getId().equals(requesterId)) {
            throw new AccessDeniedException("You do not have permission to view reports for this application.");
        }

        return weeklyReportRepository.findByApplicationId(applicationId, pageable)
                .map(WeeklyReportDto::from);
    }

    @Transactional
    public WeeklyReportDto createDraft(UUID studentId, CreateWeeklyReportDto dto) {
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application", dto.getApplicationId()));

        if (!application.getStudent().getId().equals(studentId)) {
            throw new IllegalStateException("You can only create reports for your own internship applications.");
        }

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new IllegalStateException("Weekly reports can only be submitted for approved internship applications.");
        }

        if (weeklyReportRepository.existsByApplicationIdAndWeekNumber(dto.getApplicationId(), dto.getWeekNumber())) {
            throw new IllegalStateException("A report for week " + dto.getWeekNumber() + " already exists for this application.");
        }

        Profile student = profileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", studentId));

        WeeklyReport report = WeeklyReport.builder()
                .application(application)
                .student(student)
                .weekNumber(dto.getWeekNumber())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .tasksCompleted(dto.getTasksCompleted())
                .skillsLearned(dto.getSkillsLearned() != null ? dto.getSkillsLearned() : "[]")
                .challengesAndBlockers(dto.getChallengesAndBlockers())
                .nextWeekPlan(dto.getNextWeekPlan())
                .hoursWorked(dto.getHoursWorked() != null ? dto.getHoursWorked() : 0)
                .attachmentFileId(dto.getAttachmentFileId())
                .status(ReportStatus.DRAFT)
                .build();

        return WeeklyReportDto.from(weeklyReportRepository.save(report));
    }

    @Transactional
    public WeeklyReportDto updateDraft(UUID id, UUID studentId, UpdateWeeklyReportDto dto) {
        WeeklyReport report = weeklyReportRepository.findByIdAndStudentId(id, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport", id));

        if (report.getStatus() != ReportStatus.DRAFT && report.getStatus() != ReportStatus.NEEDS_REVISION) {
            throw new IllegalStateException("Can only edit reports in DRAFT or NEEDS_REVISION status.");
        }

        if (dto.getWeekNumber() != null && !dto.getWeekNumber().equals(report.getWeekNumber())) {
            if (weeklyReportRepository.existsByApplicationIdAndWeekNumber(report.getApplication().getId(), dto.getWeekNumber())) {
                throw new IllegalStateException("A report for week " + dto.getWeekNumber() + " already exists.");
            }
            report.setWeekNumber(dto.getWeekNumber());
        }

        if (dto.getStartDate() != null) {
            report.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            report.setEndDate(dto.getEndDate());
        }
        if (dto.getTasksCompleted() != null) {
            report.setTasksCompleted(dto.getTasksCompleted());
        }
        if (dto.getSkillsLearned() != null) {
            report.setSkillsLearned(dto.getSkillsLearned());
        }
        if (dto.getChallengesAndBlockers() != null) {
            report.setChallengesAndBlockers(dto.getChallengesAndBlockers());
        }
        if (dto.getNextWeekPlan() != null) {
            report.setNextWeekPlan(dto.getNextWeekPlan());
        }
        if (dto.getHoursWorked() != null) {
            report.setHoursWorked(dto.getHoursWorked());
        }
        if (dto.getAttachmentFileId() != null) {
            report.setAttachmentFileId(dto.getAttachmentFileId());
        }

        return WeeklyReportDto.from(weeklyReportRepository.save(report));
    }

    @Transactional
    public WeeklyReportDto submitReport(UUID id, UUID studentId) {
        WeeklyReport report = weeklyReportRepository.findByIdAndStudentId(id, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport", id));

        if (report.getStatus() != ReportStatus.DRAFT && report.getStatus() != ReportStatus.NEEDS_REVISION) {
            throw new IllegalStateException("Can only submit reports that are in DRAFT or NEEDS_REVISION status.");
        }

        report.setStatus(ReportStatus.SUBMITTED);
        report.setSubmittedAt(Instant.now());

        return WeeklyReportDto.from(weeklyReportRepository.save(report));
    }

    public Page<WeeklyReportDto> getAssignedFacultyReports(UUID facultyId, ReportStatus status, Pageable pageable) {
        List<UUID> assignedStudentIds = facultyAssignmentRepository.findByFacultyId(facultyId)
                .stream()
                .map(fa -> fa.getStudent().getId())
                .toList();

        if (assignedStudentIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<WeeklyReport> reports = (status != null)
                ? weeklyReportRepository.findByStudentIdInAndStatus(assignedStudentIds, status, pageable)
                : weeklyReportRepository.findByStudentIdIn(assignedStudentIds, pageable);

        return reports.map(WeeklyReportDto::from);
    }

    @Transactional
    public WeeklyReportDto reviewReport(UUID id, UUID facultyId, boolean isAdmin, WeeklyReportReviewDto dto) {
        WeeklyReport report = weeklyReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport", id));

        if (report.getStatus() != ReportStatus.SUBMITTED && report.getStatus() != ReportStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Can only review reports that are submitted or under review.");
        }

        if (dto.getDecision() != ReportStatus.APPROVED && dto.getDecision() != ReportStatus.NEEDS_REVISION) {
            throw new IllegalStateException("Decision must be either APPROVED or NEEDS_REVISION.");
        }

        boolean isAssigned = facultyAssignmentRepository.existsByFacultyIdAndStudentId(facultyId, report.getStudent().getId());
        boolean isAppReviewer = facultyId.equals(report.getApplication().getReviewedBy());

        if (!isAdmin && !isAssigned && !isAppReviewer) {
            throw new AccessDeniedException("You are not authorized to evaluate reports for this student.");
        }

        report.setStatus(dto.getDecision());
        report.setFacultyFeedback(dto.getComments());
        report.setFacultyRating(dto.getRating());
        report.setReviewedBy(facultyId);
        report.setReviewedAt(Instant.now());

        return WeeklyReportDto.from(weeklyReportRepository.save(report));
    }
}
