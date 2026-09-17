package com.internpilot.documents;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentDto {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "File name is required")
    private String fileName;

    private String fileType;

    @NotNull(message = "File size is required")
    @Min(value = 0, message = "File size cannot be negative")
    private Long fileSize;

    @NotBlank(message = "Storage path is required")
    private String storagePath;

    private String bucketName;
}
