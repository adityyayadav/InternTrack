package com.internpilot.analytics;

import com.internpilot.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping({"/api/dashboard", "/api/v1/dashboard"})
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Student Dashboard Endpoint
     * Application counts, weekly report progress %, and active internship details.
     */
    @GetMapping("/student")
    public ResponseEntity<ApiResponse<StudentDashboardDto>> getStudentDashboard(
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getStudentDashboard(studentId)));
    }

    /**
     * Faculty Dashboard Endpoint
     * Assigned students, pending reviews, blocker alerts, and overall progress.
     */
    @GetMapping("/faculty")
    @PreAuthorize("hasAnyAuthority('ROLE_FACULTY', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<FacultyDashboardDto>> getFacultyDashboard(
            @AuthenticationPrincipal Jwt jwt) {
        UUID facultyId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getFacultyDashboard(facultyId)));
    }

    /**
     * Admin Dashboard Endpoint
     * Platform metrics, student/faculty totals, placement rates, department metrics, and monthly trends.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAdminDashboard()));
    }

    /**
     * Universal Smart Dashboard Endpoint
     * Automatically inspects authorities and delivers the appropriate dashboard.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getMyDashboard(
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        UUID userId = UUID.fromString(jwt.getSubject());

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAdminDashboard()));
        }

        boolean isFaculty = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY"));
        if (isFaculty) {
            return ResponseEntity.ok(ApiResponse.ok(dashboardService.getFacultyDashboard(userId)));
        }

        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getStudentDashboard(userId)));
    }
}
