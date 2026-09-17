package com.internpilot.notifications;

import com.internpilot.common.exception.ResourceNotFoundException;
import com.internpilot.users.Profile;
import com.internpilot.users.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public NotificationDto sendNotification(UUID recipientId, String title, String message, NotificationType type) {
        Profile recipient = profileRepository.findById(recipientId).orElse(null);
        if (recipient == null) {
            log.warn("Cannot send notification: recipient profile {} not found.", recipientId);
            return null;
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Sent {} notification to user {}: {}", type, recipientId, title);
        return NotificationDto.from(saved);
    }

    public Page<NotificationDto> getMyNotifications(UUID recipientId, Boolean unreadOnly, Pageable pageable) {
        Page<Notification> page = (unreadOnly != null && unreadOnly)
                ? notificationRepository.findByRecipient_IdAndIsReadOrderByCreatedAtDesc(recipientId, false, pageable)
                : notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(recipientId, pageable);

        return page.map(NotificationDto::from);
    }

    public long getUnreadCount(UUID recipientId) {
        return notificationRepository.countByRecipient_IdAndIsReadFalse(recipientId);
    }

    @Transactional
    public NotificationDto markAsRead(UUID id, UUID recipientId) {
        Notification notification = notificationRepository.findByIdAndRecipient_Id(id, recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));

        notification.setRead(true);
        return NotificationDto.from(notificationRepository.save(notification));
    }

    @Transactional
    public int markAllAsRead(UUID recipientId) {
        return notificationRepository.markAllAsReadByRecipientId(recipientId);
    }

    @Transactional
    public void deleteNotification(UUID id, UUID recipientId) {
        Notification notification = notificationRepository.findByIdAndRecipientId(id, recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));

        notificationRepository.delete(notification);
    }
}
