package com.internpilot.documents;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "File name must be at most 255 characters")
    private String fileName;

    private String fileType;

    @NotNull(message = "File size is required")
    @Min(value = 0, message = "File size cannot be negative")
    private Long fileSize;

    @NotBlank(message = "Storage path is required")
    @Size(max = 500, message = "Storage path must be at most 500 characters")
    private String storagePath;

    private String bucketName;
}
