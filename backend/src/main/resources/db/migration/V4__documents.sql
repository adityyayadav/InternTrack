-- V4__documents.sql
-- Document management module for file metadata and Supabase storage paths

CREATE TABLE documents (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL,
    file_name     VARCHAR(255) NOT NULL,
    file_type     VARCHAR(100),
    file_size     BIGINT NOT NULL DEFAULT 0,
    storage_path  VARCHAR(500) NOT NULL,
    bucket_name   VARCHAR(100) NOT NULL DEFAULT 'internship-documents',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_document_type CHECK (document_type IN ('RESUME', 'OFFER_LETTER', 'NOC', 'WEEKLY_ATTACHMENT', 'COMPLETION_CERTIFICATE'))
);

CREATE INDEX idx_documents_user ON documents(user_id);
CREATE INDEX idx_documents_type ON documents(document_type);
