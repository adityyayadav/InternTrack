-- V2__core_lifecycle.sql
-- Core lifecycle and workflow tables

-- Opportunities
CREATE TABLE internship_opportunities (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title                VARCHAR(255) NOT NULL,
    organization         VARCHAR(255) NOT NULL,
    description          TEXT NOT NULL,
    required_skills      JSONB DEFAULT '[]'::jsonb,
    location             VARCHAR(255) NOT NULL,
    work_mode            VARCHAR(50)  NOT NULL, -- REMOTE, ONSITE, HYBRID
    duration             VARCHAR(100),
    application_deadline TIMESTAMPTZ,
    is_active            BOOLEAN NOT NULL DEFAULT true,
    created_by           UUID REFERENCES profiles(id),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Applications
CREATE TABLE applications (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id     UUID NOT NULL REFERENCES profiles(id),
    opportunity_id UUID NOT NULL REFERENCES internship_opportunities(id),
    status         VARCHAR(50) NOT NULL DEFAULT 'DRAFT', 
    -- DRAFT, SUBMITTED, UNDER_REVIEW, CHANGES_REQUESTED, APPROVED, REJECTED
    cover_note     TEXT,
    submitted_at   TIMESTAMPTZ,
    reviewed_at    TIMESTAMPTZ,
    reviewed_by    UUID REFERENCES profiles(id),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_student_opportunity UNIQUE (student_id, opportunity_id)
);
CREATE INDEX idx_applications_student ON applications(student_id);
CREATE INDEX idx_applications_opportunity ON applications(opportunity_id);
CREATE INDEX idx_applications_status ON applications(status);

-- Application Reviews
CREATE TABLE application_reviews (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL REFERENCES applications(id),
    reviewer_id    UUID NOT NULL REFERENCES profiles(id),
    decision       VARCHAR(50) NOT NULL, -- APPROVED, REJECTED, CHANGES_REQUESTED
    comments       TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_app_reviews_application ON application_reviews(application_id);
