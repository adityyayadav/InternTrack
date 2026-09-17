-- V5__notifications.sql
-- Notification management module for user event alerts

CREATE TABLE notifications (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    title        VARCHAR(255) NOT NULL,
    message      TEXT NOT NULL,
    type         VARCHAR(50) NOT NULL,
    is_read      BOOLEAN NOT NULL DEFAULT false,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_notification_type CHECK (type IN (
        'APPLICATION_SUBMITTED',
        'APPLICATION_APPROVED',
        'APPLICATION_REJECTED',
        'WEEKLY_REPORT_DUE',
        'WEEKLY_REPORT_REVIEWED',
        'DOCUMENT_UPLOADED',
        'AI_ALERT'
    ))
);

CREATE INDEX idx_notifications_recipient ON notifications(recipient_id);
CREATE INDEX idx_notifications_is_read    ON notifications(recipient_id, is_read);
CREATE INDEX idx_notifications_created    ON notifications(created_at DESC);
