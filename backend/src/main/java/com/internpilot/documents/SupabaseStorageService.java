package com.internpilot.documents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class SupabaseStorageService {

    @Value("${app.supabase.url:}")
    private String supabaseUrl;

    @Value("${app.supabase.service-role-key:}")
    private String serviceRoleKey;

    @Value("${app.supabase.storage-bucket:internship-documents}")
    private String defaultBucket;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String getDefaultBucket() {
        return defaultBucket;
    }

    public String generateStoragePath(UUID userId, DocumentType type, String originalFilename) {
        String sanitized = sanitizeFilename(originalFilename);
        return String.format("users/%s/%s/%s_%s",
                userId,
                type.name().toLowerCase(),
                UUID.randomUUID(),
                sanitized);
    }

    public String getDownloadUrl(String bucket, String storagePath) {
        if (supabaseUrl == null || supabaseUrl.isBlank()) {
            return String.format("/api/v1/documents/storage/%s/%s", bucket, storagePath);
        }
        String baseUrl = supabaseUrl.replaceAll("/+$", "");
        return String.format("%s/storage/v1/object/public/%s/%s", baseUrl, bucket, storagePath);
    }

    public void uploadFile(String bucket, String storagePath, byte[] bytes, String contentType) {
        if (supabaseUrl == null || supabaseUrl.isBlank() || serviceRoleKey == null || serviceRoleKey.isBlank()) {
            log.info("Supabase storage endpoint not fully configured; recorded storage path {} without remote sync", storagePath);
            return;
        }

        try {
            String baseUrl = supabaseUrl.replaceAll("/+$", "");
            String endpoint = String.format("%s/storage/v1/object/%s/%s", baseUrl, bucket, storagePath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 300) {
                log.error("Failed to upload file to Supabase Storage: status {} - {}", response.statusCode(), response.body());
                throw new IllegalStateException("Failed to upload file to storage service (status " + response.statusCode() + ")");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Storage upload interrupted", e);
        } catch (Exception e) {
            log.error("Error during Supabase file upload: {}", e.getMessage(), e);
            throw new IllegalStateException("Storage service unavailable: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String bucket, String storagePath) {
        if (supabaseUrl == null || supabaseUrl.isBlank() || serviceRoleKey == null || serviceRoleKey.isBlank()) {
            return;
        }

        try {
            String baseUrl = supabaseUrl.replaceAll("/+$", "");
            String endpoint = String.format("%s/storage/v1/object/%s/%s", baseUrl, bucket, storagePath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .DELETE()
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 300 && response.statusCode() != 404) {
                log.warn("Supabase Storage file delete returned status {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("Error during Supabase file delete for {}: {}", storagePath, e.getMessage());
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unnamed_file";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
