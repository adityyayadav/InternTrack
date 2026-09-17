package com.internpilot.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDto {

    private UUID id;
    private UUID userId;
    private String userName;
    private String userEmail;
    private DocumentType documentType;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String storagePath;
    private String bucketName;
    private String downloadUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public static DocumentDto from(Document doc, String downloadUrl) {
        String userName = null;
        String userEmail = null;
        if (doc.getUser() != null) {
            userName = doc.getUser().getFullName();
            userEmail = doc.getUser().getEmail();
        }

        return DocumentDto.builder()
                .id(doc.getId())
                .userId(doc.getUser() != null ? doc.getUser().getId() : null)
                .userName(userName)
                .userEmail(userEmail)
                .documentType(doc.getDocumentType())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .storagePath(doc.getStoragePath())
                .bucketName(doc.getBucketName())
                .downloadUrl(downloadUrl)
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
