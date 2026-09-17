package com.internpilot.notifications;

import com.internpilot.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/notifications", "/api/v1/notifications"})
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /api/notifications
     * Retrieve notifications for the current authenticated user.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getMyNotifications(
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        UUID recipientId = UUID.fromString(jwt.getSubject());
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getMyNotifications(recipientId, unreadOnly, pageable)));
    }

    /**
     * GET /api/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal Jwt jwt) {
        UUID recipientId = UUID.fromString(jwt.getSubject());
        long count = notificationService.getUnreadCount(recipientId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", count)));
    }

    /**
     * PATCH /api/notifications/{id}/read
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID recipientId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(notificationService.markAsRead(id, recipientId)));
    }

    /**
     * PATCH /api/notifications/read-all
     */
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAllAsRead(
            @AuthenticationPrincipal Jwt jwt) {
        UUID recipientId = UUID.fromString(jwt.getSubject());
        int updated = notificationService.markAllAsRead(recipientId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("markedReadCount", updated)));
    }

    /**
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID recipientId = UUID.fromString(jwt.getSubject());
        notificationService.deleteNotification(id, recipientId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
