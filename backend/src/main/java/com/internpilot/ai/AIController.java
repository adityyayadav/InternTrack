package com.internpilot.ai;

import com.internpilot.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/ai", "/api/v1/ai"})
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    /**
     * 1. Resume Analysis Endpoint
     * Accepts uploaded resume text or document ID, extracts structured skills, education, and experience.
     */
    @PostMapping("/analyze-resume")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> analyzeResume(
            @Valid @RequestBody ResumeAnalysisRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = jwt != null ? UUID.fromString(jwt.getSubject()) : null;
        ResumeAnalysisResponse response = aiService.analyzeResume(studentId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 2. Internship Matching Endpoint (POST)
     * Ranks internship opportunities based on student's skills and profile.
     */
    @PostMapping("/match-internships")
    public ResponseEntity<ApiResponse<InternshipMatchResponse>> matchInternships(
            @RequestBody(required = false) InternshipMatchRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        UUID opportunityId = request != null ? request.getOpportunityId() : null;
        InternshipMatchResponse response = aiService.matchInternships(studentId, opportunityId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 2. Internship Matching Endpoint (GET convenience)
     */
    @GetMapping("/match-internships")
    public ResponseEntity<ApiResponse<InternshipMatchResponse>> getInternshipMatches(
            @RequestParam(required = false) UUID opportunityId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        InternshipMatchResponse response = aiService.matchInternships(studentId, opportunityId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 3. Weekly Report Summarization Endpoint (POST)
     * Generates a faculty summary, highlights blockers, and suggests action items.
     */
    @PostMapping("/summarize-weekly-report/{reportId}")
    public ResponseEntity<ApiResponse<WeeklyReportSummaryResponse>> summarizeWeeklyReport(
            @PathVariable UUID reportId,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        boolean isFacultyOrAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY") || a.getAuthority().equals("ROLE_ADMIN"));

        WeeklyReportSummaryResponse response = aiService.summarizeWeeklyReport(reportId, requesterId, isFacultyOrAdmin);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 3. Weekly Report Summarization Endpoint (GET convenience)
     */
    @GetMapping("/summarize-weekly-report/{reportId}")
    public ResponseEntity<ApiResponse<WeeklyReportSummaryResponse>> getWeeklyReportSummary(
            @PathVariable UUID reportId,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        boolean isFacultyOrAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY") || a.getAuthority().equals("ROLE_ADMIN"));

        WeeklyReportSummaryResponse response = aiService.summarizeWeeklyReport(reportId, requesterId, isFacultyOrAdmin);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
