-- V1__foundation.sql
-- Core tables for InternPilot Stage 1

-- Departments
CREATE TABLE departments (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    code       VARCHAR(20)  NOT NULL UNIQUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Profiles — linked to Supabase Auth user id
CREATE TABLE profiles (
    id             UUID PRIMARY KEY,  -- matches Supabase Auth user ID
    full_name      VARCHAR(150) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    role           VARCHAR(20)  NOT NULL DEFAULT 'STUDENT',
    department_id  UUID REFERENCES departments(id),
    student_number VARCHAR(50),
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_role CHECK (role IN ('STUDENT', 'FACULTY', 'ADMIN'))
);

CREATE INDEX idx_profiles_role ON profiles(role);
CREATE INDEX idx_profiles_department ON profiles(department_id);

-- Faculty assignments
CREATE TABLE faculty_assignments (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    faculty_id UUID NOT NULL REFERENCES profiles(id),
    student_id UUID NOT NULL REFERENCES profiles(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_faculty_student UNIQUE (faculty_id, student_id)
);

-- Student profiles (extended)
CREATE TABLE student_profiles (
    user_id           UUID PRIMARY KEY REFERENCES profiles(id),
    skills            JSONB DEFAULT '[]'::jsonb,
    education         TEXT,
    interests         TEXT,
    preferred_domains TEXT,
    resume_file_id    UUID,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Audit events
CREATE TABLE audit_events (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id    UUID REFERENCES profiles(id),
    entity_type VARCHAR(50)  NOT NULL,
    entity_id   UUID,
    action      VARCHAR(50)  NOT NULL,
    metadata    JSONB DEFAULT '{}'::jsonb,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_actor    ON audit_events(actor_id);
CREATE INDEX idx_audit_entity   ON audit_events(entity_type, entity_id);
CREATE INDEX idx_audit_created  ON audit_events(created_at);
