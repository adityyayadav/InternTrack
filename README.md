# InternTrack — Smart Internship Management System

InternTrack is a cloud-first, AI-enhanced platform designed to manage the end-to-end internship lifecycle for students, faculty coordinators, and administrators. 

The system leverages modern web and cloud technologies to simplify document processing, opportunity alignment, and progress tracking globally.

## 🚀 Features

- **For Students:** 
  - Manage skills and profiles.
  - Upload resumes and application documents.
  - Apply to intelligently-matched internship opportunities.
  - Submit weekly progress reports.
- **For Faculty Coordinators:**
  - Track assigned students.
  - Review submitted applications and documents.
  - Evaluate weekly progress and respond to blockers.
- **For Administrators:** 
  - Manage student/faculty routing, system config, and audit events.
- **AI Intelligence (Powered by Groq):** 
  - Resume and document text extraction.
  - Explainable opportunity matching.
  - Automated weekly-report summarization and alerts.

## 🛠 Tech Stack

- **Frontend:** React + TypeScript + Vite, styled with Tailwind CSS & shadcn/ui.
- **Backend:** Java 21 + Spring Boot 3, using Maven.
- **Database & Auth:** Supabase (PostgreSQL & Supabase Auth).
- **Core AI Integration:** LLM-assisted matching and summaries via Groq AI.
- **API Documentation:** OpenAPI / Swagger.

## 📂 Project Structure

```text
InternTrack/
├── frontend/               # React application (Vite setup)
├── backend/                # Spring Boot REST API
├── docs/                   # Additional documentation & assets
├── .env.example            # Environment variables template
└── README.md               # This file
```

## ⚙️ Getting Started

### Prerequisites
- Node.js (v18+) for frontend.
- Java 21 for backend.
- A Supabase Project (Auth, PostgreSQL DB, Storage Bucket).

### Installation & Run

1. **Clone the repository.**
2. **Environment Configuration:**
   - Copy `.env.example` to `backend/.env` and `frontend/.env.local`.
   - Fill in your Supabase keys and database connection strings.

3. **Backend Startup (Spring Boot):**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   *(The database schema migrates automatically on startup via Flyway).*

4. **Frontend Startup (React):**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 🔐 Security
- Backend endpoints are secured using Spring Security, validating Supabase JWTs directly against Supabase's JWKS.
- Role-based authorization (`STUDENT`, `FACULTY`, `ADMIN`) is strictly enforced at the API layer.
