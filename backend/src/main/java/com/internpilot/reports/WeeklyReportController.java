package com.internpilot.reports;

import com.internpilot.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/weekly-reports")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<WeeklyReportDto>>> getMyReports(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(weeklyReportService.getStudentReports(studentId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WeeklyReportDto>> getReportById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        boolean isFacultyOrAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY") || a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(ApiResponse.ok(weeklyReportService.getReportById(id, requesterId, isFacultyOrAdmin)));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ApiResponse<Page<WeeklyReportDto>>> getReportsByApplication(
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        boolean isFacultyOrAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY") || a.getAuthority().equals("ROLE_ADMIN"));

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(weeklyReportService.getReportsByApplication(applicationId, requesterId, isFacultyOrAdmin, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WeeklyReportDto>> createDraft(
            @Valid @RequestBody CreateWeeklyReportDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        WeeklyReportDto created = weeklyReportService.createDraft(studentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WeeklyReportDto>> updateDraft(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWeeklyReportDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(weeklyReportService.updateDraft(id, studentId, dto)));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<WeeklyReportDto>> submitReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(weeklyReportService.submitReport(id, studentId)));
    }
}
