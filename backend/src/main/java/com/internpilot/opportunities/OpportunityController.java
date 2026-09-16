package com.internpilot.opportunities;

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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    // Public / Student endpoints
    @GetMapping("/opportunities")
    public ResponseEntity<ApiResponse<Page<OpportunityDto>>> getOpportunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OpportunityDto> result = opportunityService.getAllActiveOpportunities(pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/opportunities/{id}")
    public ResponseEntity<ApiResponse<OpportunityDto>> getOpportunityById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(opportunityService.getOpportunityById(id)));
    }

    // Admin endpoints
    @PostMapping("/admin/opportunities")
    public ResponseEntity<ApiResponse<OpportunityDto>> createOpportunity(
            @Valid @RequestBody OpportunityDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID adminId = UUID.fromString(jwt.getSubject());
        OpportunityDto created = opportunityService.createOpportunity(dto, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/admin/opportunities/{id}")
    public ResponseEntity<ApiResponse<OpportunityDto>> updateOpportunity(
            @PathVariable UUID id,
            @Valid @RequestBody OpportunityDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(opportunityService.updateOpportunity(id, dto)));
    }
}
