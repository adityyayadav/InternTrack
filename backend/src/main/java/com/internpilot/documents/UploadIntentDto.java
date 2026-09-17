package com.internpilot.documents;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UploadIntentDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "Document type is required")
        private DocumentType documentType;

        @NotBlank(message = "File name is required")
        @Size(max = 255, message = "File name must be at most 255 characters")
        private String fileName;

        private String fileType;

        @Min(value = 0, message = "File size cannot be negative")
        private Long fileSize;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String storagePath;
        private String bucketName;
        private String downloadUrl;
        private DocumentType documentType;
        private String fileName;
    }
}
