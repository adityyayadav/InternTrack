-- V3__weekly_reports.sql
-- Weekly reports module for student internship progress tracking and faculty evaluation

CREATE TABLE weekly_reports (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id          UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    student_id              UUID NOT NULL REFERENCES profiles(id),
    week_number             INT NOT NULL,
    start_date              DATE NOT NULL,
    end_date                DATE NOT NULL,
    tasks_completed         TEXT NOT NULL,
    skills_learned          JSONB DEFAULT '[]'::jsonb,
    challenges_and_blockers TEXT,
    next_week_plan          TEXT,
    hours_worked            INT NOT NULL DEFAULT 0,
    attachment_file_id      UUID,

    -- Review and evaluation
    status                  VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    faculty_feedback        TEXT,
    faculty_rating          INT,
    reviewed_by             UUID REFERENCES profiles(id),
    reviewed_at             TIMESTAMPTZ,
    submitted_at            TIMESTAMPTZ,

    -- AI summarization and blocker detection flags
    ai_summary              TEXT,
    ai_risk_flag            VARCHAR(50),

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_application_week UNIQUE (application_id, week_number),
    CONSTRAINT chk_report_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'NEEDS_REVISION', 'APPROVED')),
    CONSTRAINT chk_report_rating CHECK (faculty_rating IS NULL OR (faculty_rating >= 1 AND faculty_rating <= 5))
);

CREATE INDEX idx_weekly_reports_app      ON weekly_reports(application_id);
CREATE INDEX idx_weekly_reports_student  ON weekly_reports(student_id);
CREATE INDEX idx_weekly_reports_status   ON weekly_reports(status);
CREATE INDEX idx_weekly_reports_reviewer ON weekly_reports(reviewed_by);
