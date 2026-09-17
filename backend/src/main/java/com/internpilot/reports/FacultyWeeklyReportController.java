package com.internpilot.reports;

import com.internpilot.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/faculty/weekly-reports")
@RequiredArgsConstructor
public class FacultyWeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WeeklyReportDto>>> getAssignedStudentReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        UUID facultyId = UUID.fromString(jwt.getSubject());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(weeklyReportService.getAssignedFacultyReports(facultyId, status, pageable)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ApiResponse<WeeklyReportDto>> reviewReport(
            @PathVariable UUID id,
            @Valid @RequestBody WeeklyReportReviewDto dto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        UUID facultyId = UUID.fromString(jwt.getSubject());
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(ApiResponse.ok(weeklyReportService.reviewReport(id, facultyId, isAdmin, dto)));
    }
}
