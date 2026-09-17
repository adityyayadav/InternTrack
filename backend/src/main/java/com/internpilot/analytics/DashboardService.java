package com.internpilot.analytics;

import com.internpilot.applications.Application;
import com.internpilot.applications.ApplicationRepository;
import com.internpilot.applications.ApplicationStatus;
import com.internpilot.departments.Department;
import com.internpilot.departments.DepartmentRepository;
import com.internpilot.opportunities.OpportunityRepository;
import com.internpilot.reports.ReportStatus;
import com.internpilot.reports.WeeklyReport;
import com.internpilot.reports.WeeklyReportRepository;
import com.internpilot.users.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final ApplicationRepository applicationRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final OpportunityRepository opportunityRepository;
    private final ProfileRepository profileRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyAssignmentRepository facultyAssignmentRepository;

    private static final double STANDARD_WEEKS_TARGET = 12.0;

    /**
     * 1. Student Dashboard
     */
    public StudentDashboardDto getStudentDashboard(UUID studentId) {
        long totalApps = applicationRepository.countByStudentId(studentId);
        long acceptedApps = applicationRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.APPROVED);
        long rejectedApps = applicationRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.REJECTED);
        long draftApps = applicationRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.DRAFT);

        long pendingApps = applicationRepository.countByStudentIdInAndStatusIn(
                List.of(studentId),
                List.of(ApplicationStatus.SUBMITTED, ApplicationStatus.UNDER_REVIEW, ApplicationStatus.CHANGES_REQUESTED));

        long totalWeeklyReports = weeklyReportRepository.countByStudentId(studentId);
        long approvedWeeklyReports = weeklyReportRepository.countByStudentIdAndStatus(studentId, ReportStatus.APPROVED);

        double completionPercentage = Math.round(Math.min(100.0, (approvedWeeklyReports * 100.0) / STANDARD_WEEKS_TARGET) * 10.0) / 10.0;

        List<Application> activeApps = applicationRepository.findByStudentIdAndStatus(studentId, ApplicationStatus.APPROVED);
        List<StudentDashboardDto.UpcomingInternshipDto> activeInternships = activeApps.stream()
                .map(app -> StudentDashboardDto.UpcomingInternshipDto.builder()
                        .applicationId(app.getId())
                        .opportunityId(app.getOpportunity() != null ? app.getOpportunity().getId() : null)
                        .opportunityTitle(app.getOpportunity() != null ? app.getOpportunity().getTitle() : null)
                        .organization(app.getOpportunity() != null ? app.getOpportunity().getOrganization() : null)
                        .location(app.getOpportunity() != null ? app.getOpportunity().getLocation() : null)
                        .workMode(app.getOpportunity() != null ? app.getOpportunity().getWorkMode() : null)
                        .duration(app.getOpportunity() != null ? app.getOpportunity().getDuration() : null)
                        .status(app.getStatus().name())
                        .build())
                .toList();

        return StudentDashboardDto.builder()
                .totalApplications(totalApps)
                .acceptedCount(acceptedApps)
                .pendingCount(pendingApps)
                .rejectedCount(rejectedApps)
                .draftCount(draftApps)
                .weeklyReportCompletionPercentage(completionPercentage)
                .totalWeeklyReports(totalWeeklyReports)
                .approvedWeeklyReports(approvedWeeklyReports)
                .activeInternships(activeInternships)
                .build();
    }

    /**
     * 2. Faculty Dashboard
     */
    public FacultyDashboardDto getFacultyDashboard(UUID facultyId) {
        List<FacultyAssignment> assignments = facultyAssignmentRepository.findByFacultyId(facultyId);
        if (assignments.isEmpty()) {
            return FacultyDashboardDto.builder()
                    .assignedStudentsCount(0)
                    .pendingApplicationReviewsCount(0)
                    .pendingWeeklyReportReviewsCount(0)
                    .studentsWithBlockersCount(0)
                    .overallInternshipProgressPercentage(0.0)
                    .assignedStudents(Collections.emptyList())
                    .activeBlockers(Collections.emptyList())
                    .build();
        }

        List<UUID> studentIds = assignments.stream()
                .map(a -> a.getStudent().getId())
                .toList();

        long pendingAppReviews = applicationRepository.countByStudentIdInAndStatusIn(
                studentIds,
                List.of(ApplicationStatus.SUBMITTED, ApplicationStatus.UNDER_REVIEW));

        long pendingReportReviews = weeklyReportRepository.countByStudentIdInAndStatusIn(
                studentIds,
                List.of(ReportStatus.SUBMITTED, ReportStatus.UNDER_REVIEW));

        List<WeeklyReport> allAssignedReports = weeklyReportRepository.findByStudentIdIn(studentIds);

        // Filter active blockers
        List<WeeklyReport> blockerReports = allAssignedReports.stream()
                .filter(r -> r.getStatus() != ReportStatus.APPROVED)
                .filter(r -> (r.getChallengesAndBlockers() != null && !r.getChallengesAndBlockers().isBlank() && !"none".equalsIgnoreCase(r.getChallengesAndBlockers().trim()))
                        || "HIGH".equalsIgnoreCase(r.getAiRiskFlag())
                        || "BLOCKER_DETECTED".equalsIgnoreCase(r.getAiRiskFlag()))
                .toList();

        long studentsWithBlockersCount = blockerReports.stream()
                .map(r -> r.getStudent().getId())
                .distinct()
                .count();

        List<FacultyDashboardDto.BlockerAlertDto> activeBlockers = blockerReports.stream()
                .map(r -> FacultyDashboardDto.BlockerAlertDto.builder()
                        .reportId(r.getId())
                        .studentId(r.getStudent().getId())
                        .studentName(r.getStudent().getFullName())
                        .weekNumber(r.getWeekNumber())
                        .blockerDescription(r.getChallengesAndBlockers())
                        .riskLevel(r.getAiRiskFlag() != null ? r.getAiRiskFlag() : "MEDIUM")
                        .status(r.getStatus().name())
                        .build())
                .toList();

        // Build individual student progress summaries
        List<FacultyDashboardDto.StudentSummaryDto> studentSummaries = new ArrayList<>();
        double totalProgressSum = 0.0;
        int activeInternshipCount = 0;

        for (FacultyAssignment assignment : assignments) {
            Profile student = assignment.getStudent();
            List<Application> approvedApps = applicationRepository.findByStudentIdAndStatus(student.getId(), ApplicationStatus.APPROVED);

            long completedReports = weeklyReportRepository.countByStudentIdAndStatus(student.getId(), ReportStatus.APPROVED);
            double progress = Math.round(Math.min(100.0, (completedReports * 100.0) / STANDARD_WEEKS_TARGET) * 10.0) / 10.0;

            String oppTitle = null;
            String org = null;
            String appStatus = "NO_INTERNSHIP";

            if (!approvedApps.isEmpty()) {
                Application currentApp = approvedApps.get(0);
                oppTitle = currentApp.getOpportunity() != null ? currentApp.getOpportunity().getTitle() : null;
                org = currentApp.getOpportunity() != null ? currentApp.getOpportunity().getOrganization() : null;
                appStatus = currentApp.getStatus().name();
                totalProgressSum += progress;
                activeInternshipCount++;
            }

            studentSummaries.add(FacultyDashboardDto.StudentSummaryDto.builder()
                    .studentId(student.getId())
                    .fullName(student.getFullName())
                    .email(student.getEmail())
                    .studentNumber(student.getStudentNumber())
                    .internshipTitle(oppTitle)
                    .organization(org)
                    .applicationStatus(appStatus)
                    .completedReports(completedReports)
                    .progressPercentage(progress)
                    .build());
        }

        double overallProgress = activeInternshipCount > 0
                ? Math.round((totalProgressSum / activeInternshipCount) * 10.0) / 10.0
                : 0.0;

        return FacultyDashboardDto.builder()
                .assignedStudentsCount(assignments.size())
                .pendingApplicationReviewsCount(pendingAppReviews)
                .pendingWeeklyReportReviewsCount(pendingReportReviews)
                .studentsWithBlockersCount(studentsWithBlockersCount)
                .overallInternshipProgressPercentage(overallProgress)
                .assignedStudents(studentSummaries)
                .activeBlockers(activeBlockers)
                .build();
    }

    /**
     * 3. Admin Dashboard
     */
    public AdminDashboardDto getAdminDashboard() {
        long totalStudents = profileRepository.countByRole(Role.STUDENT);
        long totalFaculty = profileRepository.countByRole(Role.FACULTY);
        long activeOpportunities = opportunityRepository.countByIsActiveTrue();
        long totalApplications = applicationRepository.count();
        long activeInternships = applicationRepository.countByStatus(ApplicationStatus.APPROVED);

        long placedStudents = applicationRepository.countDistinctPlacedStudents();
        long seekingStudents = Math.max(0, totalStudents - placedStudents);
        double placementRate = totalStudents > 0
                ? Math.round(((placedStudents * 100.0) / totalStudents) * 10.0) / 10.0
                : 0.0;

        AdminDashboardDto.PlacementStatsDto placementStats = AdminDashboardDto.PlacementStatsDto.builder()
                .placedStudents(placedStudents)
                .seekingStudents(seekingStudents)
                .placementRatePercentage(placementRate)
                .build();

        // Department-wise internship count
        List<Department> departments = departmentRepository.findAll();
        Map<UUID, Long> deptInternshipCount = new HashMap<>();

        List<Application> allApproved = applicationRepository.findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                .toList();

        for (Application app : allApproved) {
            if (app.getStudent() != null && app.getStudent().getDepartmentId() != null) {
                deptInternshipCount.merge(app.getStudent().getDepartmentId(), 1L, Long::sum);
            }
        }

        List<AdminDashboardDto.DepartmentMetricDto> departmentMetrics = departments.stream()
                .map(dept -> AdminDashboardDto.DepartmentMetricDto.builder()
                        .departmentName(dept.getName())
                        .departmentCode(dept.getCode())
                        .internshipCount(deptInternshipCount.getOrDefault(dept.getId(), 0L))
                        .build())
                .toList();

        // Monthly application trends
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneId.of("UTC"));
        Map<String, Long> monthlyCounts = new TreeMap<>();

        List<Application> allApps = applicationRepository.findAll();
        for (Application app : allApps) {
            if (app.getCreatedAt() != null) {
                String monthKey = formatter.format(app.getCreatedAt());
                monthlyCounts.merge(monthKey, 1L, Long::sum);
            }
        }

        List<AdminDashboardDto.MonthlyTrendDto> monthlyTrends = monthlyCounts.entrySet().stream()
                .map(e -> AdminDashboardDto.MonthlyTrendDto.builder()
                        .month(e.getKey())
                        .applicationCount(e.getValue())
                        .build())
                .toList();

        return AdminDashboardDto.builder()
                .totalStudents(totalStudents)
                .totalFaculty(totalFaculty)
                .activeOpportunities(activeOpportunities)
                .totalApplications(totalApplications)
                .activeInternships(activeInternships)
                .placementStats(placementStats)
                .departmentInternships(departmentMetrics)
                .monthlyTrends(monthlyTrends)
                .build();
    }
}
