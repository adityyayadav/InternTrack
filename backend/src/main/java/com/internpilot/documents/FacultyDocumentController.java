package com.internpilot.documents;

import com.internpilot.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/faculty/documents")
@RequiredArgsConstructor
public class FacultyDocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DocumentDto>>> getAssignedStudentDocuments(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) DocumentType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        UUID facultyId = UUID.fromString(jwt.getSubject());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(documentService.getAssignedFacultyDocuments(facultyId, studentId, type, pageable)));
    }
}
