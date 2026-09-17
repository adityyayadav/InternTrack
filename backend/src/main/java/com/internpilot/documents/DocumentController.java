package com.internpilot.documents;

import com.internpilot.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload-intent")
    public ResponseEntity<ApiResponse<UploadIntentDto.Response>> createUploadIntent(
            @Valid @RequestBody UploadIntentDto.Request request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UploadIntentDto.Response response = documentService.createUploadIntent(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DocumentDto>> registerDocument(
            @Valid @RequestBody CreateDocumentDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        DocumentDto saved = documentService.registerDocument(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(saved));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentDto>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        DocumentDto saved = documentService.uploadDocument(userId, documentType, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(saved));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<DocumentDto>>> getMyDocuments(
            @RequestParam(required = false) DocumentType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(documentService.getMyDocuments(userId, type, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentDto>> getDocumentById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        boolean isFacultyOrAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY") || a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(ApiResponse.ok(documentService.getDocumentById(id, requesterId, isFacultyOrAdmin)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        documentService.deleteDocument(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
