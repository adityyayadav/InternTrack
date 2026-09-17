# InternTrack Backend Completion Report

## Architecture Overview

InternTrack is a Java 21 Spring Boot 3.3.6 REST backend organized by business module under `com.internpilot`.

- **Web/API:** Spring MVC controllers returning a consistent `ApiResponse<T>` envelope.
- **Business logic:** Module services with transactional workflow operations.
- **Persistence:** Spring Data JPA repositories and PostgreSQL.
- **Schema management:** Flyway migrations with Hibernate validation (`ddl-auto: validate`).
- **Authentication:** Spring Security OAuth2 Resource Server with Supabase JWTs.
- **Storage:** Supabase Storage integration for document objects and metadata.
- **AI integration:** Groq-compatible client through the AI service module.
- **API documentation:** Springdoc OpenAPI and Swagger UI.
- **Error handling:** Central `GlobalExceptionHandler` for domain, authorization, validation, malformed request, and unexpected errors.

## Database Schema and Flyway Versions

- **V1 - foundation:** Departments, profiles, faculty assignments, student profiles, and audit events. Includes role, relationship, actor, entity, and timestamp indexes.
- **V2 - core lifecycle:** Internship opportunities, applications, and application reviews, including uniqueness and status constraints.
- **V3 - weekly reports:** Student weekly reports, faculty review fields, ratings, AI summary/risk fields, and workflow indexes.
- **V4 - documents:** Document metadata, storage paths, buckets, document type constraints, and user/type indexes.
- **V5 - notifications:** User notifications, read state, notification types, and recipient/read/time indexes.

The Java audit model maps to the existing `audit_events` table; no duplicate audit migration was required.

## REST Endpoints by Module

### Health

- `GET /api/v1/health`

### Users and Profiles

- `GET /api/v1/me`
- `GET /api/v1/students/me/profile`
- `PUT /api/v1/students/me/profile`

### Opportunities

- `GET /api/v1/opportunities`
- `GET /api/v1/opportunities/{id}`
- `POST /api/v1/admin/opportunities`
- `PUT /api/v1/admin/opportunities/{id}`

### Applications

- `GET /api/v1/applications/me`
- `GET /api/v1/applications/{id}`
- `POST /api/v1/applications`
- `POST /api/v1/applications/{id}/submit`
- `POST /api/v1/applications/faculty/{id}/decision`

### Weekly Reports

- `GET /api/v1/weekly-reports/me`
- `GET /api/v1/weekly-reports/{id}`
- `GET /api/v1/weekly-reports/application/{applicationId}`
- `POST /api/v1/weekly-reports`
- `PUT /api/v1/weekly-reports/{id}`
- `POST /api/v1/weekly-reports/{id}/submit`
- `GET /api/v1/faculty/weekly-reports`
- `POST /api/v1/faculty/weekly-reports/{id}/review`

### Documents

- `POST /api/v1/documents/upload-intent`
- `POST /api/v1/documents`
- `POST /api/v1/documents/upload`
- `GET /api/v1/documents/me`
- `GET /api/v1/documents/{id}`
- `DELETE /api/v1/documents/{id}`
- `GET /api/v1/faculty/documents`

### Notifications

Available under both `/api/notifications` and `/api/v1/notifications`:

- `GET /notifications`
- `GET /notifications/unread-count`
- `PATCH /notifications/{id}/read`
- `PATCH /notifications/read-all`
- `DELETE /notifications/{id}`

### Analytics Dashboards

Available under both `/api/dashboard` and `/api/v1/dashboard`:

- `GET /dashboard/student`
- `GET /dashboard/faculty`
- `GET /dashboard/admin`
- `GET /dashboard/me`

### AI

Available under both `/api/ai` and `/api/v1/ai`:

- `POST /ai/analyze-resume`
- `POST /ai/match-internships`
- `GET /ai/match-internships`
- `POST /ai/summarize-weekly-report/{reportId}`
- `GET /ai/summarize-weekly-report/{reportId}`

### Audit Log

- `GET /api/v1/admin/audit-events`
  - Optional filters: `actorId`, `entityType`
  - Paginated and sorted newest first

### OpenAPI

- `GET /api-docs`
- Swagger UI: `/swagger-ui.html`

## Security Flow

1. The client sends a Supabase access token using the `Authorization: Bearer <JWT>` header.
2. Spring Security validates the JWT using Supabase's JWKS endpoint.
3. `SupabaseJwtAuthConverter` reads the role from `app_metadata.role` or `user_metadata.role`, defaulting to `ROLE_STUDENT`.
4. Stateless security and CSRF disabled API handling are configured for REST usage.
5. Public routes include health and Swagger resources; API routes otherwise require authentication.
6. Admin routes require `ROLE_ADMIN`; faculty routes require `ROLE_FACULTY` or `ROLE_ADMIN`.
7. Method security supports additional `@PreAuthorize` checks on dashboard operations.
8. Swagger/OpenAPI documents JWT bearer authentication through the `bearerAuth` security scheme.

## AI Features

- Resume analysis for structured skills, education, and experience extraction.
- Internship matching based on student profile and opportunity data.
- Weekly report summarization, blocker detection, risk signaling, and suggested actions.
- Groq-compatible provider configuration is externalized through application properties and environment variables.

## Audit Logging

The audit module provides:

- JPA persistence for actor, entity type, entity ID, action, metadata, and creation time.
- Transactional audit recording through `AuditEventService`.
- Admin-only paginated audit retrieval with actor/entity filters.
- Audit records for application creation, submission, and review.
- Audit records for opportunity creation and updates.
- Audit records for student profile updates.

## Test Coverage Summary

The backend currently includes:

- `AuditEventServiceTest`: verifies audit event fields are persisted through the repository.
- `ApplicationControllerIntegrationTest`: verifies invalid application payloads return the standard validation error response and do not invoke the service.
- A dedicated `test` profile supplies local placeholder configuration only; no real Supabase, database, storage, or AI secrets are used.

Validation status:

- `mvn clean test`: passed, 2 tests, 0 failures, 0 errors.
- `mvn verify`: passed, including tests, jar packaging, and Spring Boot repackaging.

## Remaining Future Enhancements

- Expand unit coverage across services, authorization branches, and state-transition rules.
- Add Testcontainers-based PostgreSQL and Flyway integration tests.
- Add JWT authentication and role authorization tests with mock JWTs.
- Add contract tests for OpenAPI response schemas and pagination behavior.
- Add audit coverage for document, weekly report, notification, and administrative mutations.
- Add structured JSON metadata construction instead of accepting raw metadata strings.
- Add rate limiting, request correlation IDs, and production observability dashboards.
- Add scheduled cleanup/retention policies for audit events and uploaded documents.
- Add resilient AI provider timeouts, retries, circuit breaking, and usage monitoring.
- Add production secret management and environment validation at startup.
