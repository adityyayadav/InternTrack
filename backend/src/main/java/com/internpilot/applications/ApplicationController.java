package com.internpilot.applications;

import com.internpilot.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> getMyApplications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getStudentApplications(studentId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDto>> getApplicationById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplicationById(id, studentId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationDto>> createDraft(
            @Valid @RequestBody ApplicationDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        ApplicationDto created = applicationService.createDraft(dto, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<ApplicationDto>> submitApplication(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID studentId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(applicationService.submitApplication(id, studentId, dto)));
    }

    // Faculty Endpoints
    @PostMapping("/faculty/{id}/decision")
    public ResponseEntity<ApiResponse<ApplicationDto>> reviewApplication(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationReviewDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID facultyId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(applicationService.reviewApplication(id, facultyId, dto)));
    }
}
