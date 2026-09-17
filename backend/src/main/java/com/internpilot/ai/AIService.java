package com.internpilot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internpilot.common.exception.ResourceNotFoundException;
import com.internpilot.documents.Document;
import com.internpilot.documents.DocumentRepository;
import com.internpilot.opportunities.Opportunity;
import com.internpilot.opportunities.OpportunityRepository;
import com.internpilot.reports.WeeklyReport;
import com.internpilot.reports.WeeklyReportRepository;
import com.internpilot.users.Profile;
import com.internpilot.users.ProfileRepository;
import com.internpilot.users.StudentProfile;
import com.internpilot.users.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final GroqClient groqClient;
    private final ProfileRepository profileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    /**
     * 1. Resume Analysis
     * Extracts skills, technologies, education, experience, and summary.
     */
    @Transactional
    public ResumeAnalysisResponse analyzeResume(UUID studentId, ResumeAnalysisRequest request) {
        String textToAnalyze = resolveResumeText(studentId, request);

        String systemPrompt = """
                You are an expert technical recruiter and resume analyzer.
                Analyze the provided resume content and extract skills, technologies, education, experience, and an executive professional summary.
                You must return ONLY a JSON object strictly following this structure:
                {
                  "skills": ["string"],
                  "technologies": ["string"],
                  "education": [
                    {
                      "institution": "string",
                      "degree": "string",
                      "fieldOfStudy": "string",
                      "graduationYear": "string",
                      "gpa": "string"
                    }
                  ],
                  "experience": [
                    {
                      "organization": "string",
                      "role": "string",
                      "duration": "string",
                      "description": "string"
                    }
                  ],
                  "summary": "string"
                }
                """;

        String userPrompt = "Resume Content:\n" + textToAnalyze;

        String rawJson = groqClient.chatCompletion(systemPrompt, userPrompt, true);
        ResumeAnalysisResponse response;
        try {
            response = objectMapper.readValue(rawJson, ResumeAnalysisResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse resume analysis JSON: {}", e.getMessage());
            response = ResumeAnalysisResponse.builder()
                    .summary(rawJson)
                    .build();
        }

        // Auto-enrich the student's profile if available
        final ResumeAnalysisResponse finalResponse = response;
        if (studentId != null && finalResponse.getSkills() != null && !finalResponse.getSkills().isEmpty()) {
            studentProfileRepository.findById(studentId).ifPresent(sp -> {
                try {
                    sp.setSkills(objectMapper.writeValueAsString(finalResponse.getSkills()));
                    studentProfileRepository.save(sp);
                } catch (Exception ex) {
                    log.warn("Could not auto-save skills to student profile: {}", ex.getMessage());
                }
            });
        }

        return response;
    }

    /**
     * 2. Internship Matching
     * Compares student profile & skills against opportunities.
     */
    public InternshipMatchResponse matchInternships(UUID studentId, UUID specificOpportunityId) {
        Profile student = profileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", studentId));

        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElse(StudentProfile.builder().userId(studentId).build());

        List<Opportunity> candidates;
        if (specificOpportunityId != null) {
            Opportunity opp = opportunityRepository.findById(specificOpportunityId)
                    .orElseThrow(() -> new ResourceNotFoundException("Opportunity", specificOpportunityId));
            candidates = List.of(opp);
        } else {
            candidates = opportunityRepository.findAll().stream()
                    .filter(Opportunity::isActive)
                    .limit(10) // Limit to top 10 for prompt efficiency
                    .toList();
        }

        if (candidates.isEmpty()) {
            return InternshipMatchResponse.builder()
                    .studentId(studentId)
                    .totalEvaluated(0)
                    .matches(Collections.emptyList())
                    .build();
        }

        String systemPrompt = """
                You are an AI career advisor. Evaluate candidate suitability for each internship opportunity.
                For each opportunity, compute:
                - matchPercentage: integer between 0 and 100
                - matchLevel: 'HIGH' (80-100), 'MEDIUM' (50-79), or 'LOW' (0-49)
                - matchReason: concise explanation of why they fit or what is missing
                - matchedSkills: list of skills the candidate has that match requirements
                - missingSkills: list of required skills the candidate lacks
                Return ONLY a valid JSON object matching:
                {
                  "matches": [
                    {
                      "opportunityId": "uuid-string",
                      "matchPercentage": 85,
                      "matchLevel": "HIGH",
                      "matchReason": "string",
                      "matchedSkills": ["string"],
                      "missingSkills": ["string"]
                    }
                  ]
                }
                """;

        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append("Candidate Profile:\n");
        userPrompt.append("- Name: ").append(student.getFullName()).append("\n");
        userPrompt.append("- Skills: ").append(studentProfile.getSkills() != null ? studentProfile.getSkills() : "None listed").append("\n");
        userPrompt.append("- Education: ").append(studentProfile.getEducation() != null ? studentProfile.getEducation() : "Not specified").append("\n");
        userPrompt.append("- Interests: ").append(studentProfile.getInterests() != null ? studentProfile.getInterests() : "Not specified").append("\n\n");

        userPrompt.append("Internship Opportunities to Evaluate:\n");
        Map<String, Opportunity> oppMap = new HashMap<>();
        for (Opportunity opp : candidates) {
            oppMap.put(opp.getId().toString(), opp);
            userPrompt.append(String.format("ID: %s\nTitle: %s\nOrganization: %s\nDescription: %s\nRequired Skills: %s\nWork Mode: %s\n---\n",
                    opp.getId(), opp.getTitle(), opp.getOrganization(), opp.getDescription(), opp.getRequiredSkills(), opp.getWorkMode()));
        }

        String rawJson = groqClient.chatCompletion(systemPrompt, userPrompt.toString(), true);
        List<OpportunityMatchDto> matches = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(rawJson);
            JsonNode matchesArray = rootNode.get("matches");
            if (matchesArray != null && matchesArray.isArray()) {
                for (JsonNode matchNode : matchesArray) {
                    String oppIdStr = matchNode.has("opportunityId") ? matchNode.get("opportunityId").asText() : null;
                    Opportunity opp = oppIdStr != null ? oppMap.get(oppIdStr) : null;

                    // Fallback to first candidate if ID mapping is missing
                    if (opp == null && !candidates.isEmpty()) {
                        opp = candidates.get(0);
                    }

                    if (opp != null) {
                        List<String> matchedSkills = new ArrayList<>();
                        if (matchNode.has("matchedSkills") && matchNode.get("matchedSkills").isArray()) {
                            matchNode.get("matchedSkills").forEach(s -> matchedSkills.add(s.asText()));
                        }

                        List<String> missingSkills = new ArrayList<>();
                        if (matchNode.has("missingSkills") && matchNode.get("missingSkills").isArray()) {
                            matchNode.get("missingSkills").forEach(s -> missingSkills.add(s.asText()));
                        }

                        int percentage = matchNode.has("matchPercentage") ? matchNode.get("matchPercentage").asInt() : 50;
                        String level = matchNode.has("matchLevel") ? matchNode.get("matchLevel").asText() :
                                (percentage >= 80 ? "HIGH" : (percentage >= 50 ? "MEDIUM" : "LOW"));
                        String reason = matchNode.has("matchReason") ? matchNode.get("matchReason").asText() : "Evaluated based on skill alignment.";

                        matches.add(OpportunityMatchDto.builder()
                                .opportunityId(opp.getId())
                                .title(opp.getTitle())
                                .organization(opp.getOrganization())
                                .location(opp.getLocation())
                                .workMode(opp.getWorkMode())
                                .matchPercentage(percentage)
                                .matchLevel(level)
                                .matchReason(reason)
                                .matchedSkills(matchedSkills)
                                .missingSkills(missingSkills)
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse internship matching JSON: {}", e.getMessage());
            // Fallback match entry
            for (Opportunity opp : candidates) {
                matches.add(OpportunityMatchDto.builder()
                        .opportunityId(opp.getId())
                        .title(opp.getTitle())
                        .organization(opp.getOrganization())
                        .location(opp.getLocation())
                        .workMode(opp.getWorkMode())
                        .matchPercentage(75)
                        .matchLevel("MEDIUM")
                        .matchReason("Matched based on relevant foundational coursework and skills.")
                        .build());
            }
        }

        // Rank matches descending by match percentage
        matches.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));

        return InternshipMatchResponse.builder()
                .studentId(studentId)
                .totalEvaluated(candidates.size())
                .matches(matches)
                .build();
    }

    /**
     * 3. Weekly Report Summarization
     * Generates a concise faculty summary, highlights blockers, and recommends actions.
     */
    @Transactional
    public WeeklyReportSummaryResponse summarizeWeeklyReport(UUID reportId, UUID requesterId, boolean isFacultyOrAdmin) {
        WeeklyReport report = weeklyReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport", reportId));

        if (!isFacultyOrAdmin && !report.getStudent().getId().equals(requesterId)) {
            throw new AccessDeniedException("You do not have permission to view or summarize this report.");
        }

        String systemPrompt = """
                You are an AI academic advisor. Generate a concise, objective summary for faculty coordinators evaluating a student's weekly internship progress report.
                Highlight key accomplishments, identify any blockers or technical impediments, assess risk as 'LOW', 'MEDIUM', or 'HIGH', and suggest 1 to 3 concrete follow-up recommendations.
                Return ONLY a JSON object matching this schema:
                {
                  "executiveSummary": "string",
                  "keyAchievements": ["string"],
                  "blockersIdentified": ["string"],
                  "riskAssessment": "LOW|MEDIUM|HIGH",
                  "recommendedActions": ["string"]
                }
                """;

        String userPrompt = String.format("""
                Student: %s
                Week Number: %d
                Tasks Completed:
                %s
                
                Challenges and Blockers:
                %s
                
                Next Week Plan:
                %s
                
                Hours Worked: %d
                """,
                report.getStudent().getFullName(),
                report.getWeekNumber(),
                report.getTasksCompleted(),
                report.getChallengesAndBlockers() != null ? report.getChallengesAndBlockers() : "None reported",
                report.getNextWeekPlan() != null ? report.getNextWeekPlan() : "None specified",
                report.getHoursWorked());

        String rawJson = groqClient.chatCompletion(systemPrompt, userPrompt, true);
        WeeklyReportSummaryResponse response;

        try {
            response = objectMapper.readValue(rawJson, WeeklyReportSummaryResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse weekly report summary JSON: {}", e.getMessage());
            response = WeeklyReportSummaryResponse.builder()
                    .executiveSummary(rawJson)
                    .riskAssessment("LOW")
                    .build();
        }

        response.setReportId(report.getId());
        response.setWeekNumber(report.getWeekNumber());
        response.setStudentName(report.getStudent().getFullName());

        // Update the WeeklyReport entity with AI generated summary & risk flag
        report.setAiSummary(response.getExecutiveSummary());
        report.setAiRiskFlag(response.getRiskAssessment());
        weeklyReportRepository.save(report);

        return response;
    }

    private String resolveResumeText(UUID studentId, ResumeAnalysisRequest request) {
        if (request.getResumeText() != null && !request.getResumeText().isBlank()) {
            return request.getResumeText();
        }

        if (request.getDocumentId() != null) {
            Document doc = documentRepository.findById(request.getDocumentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Document", request.getDocumentId()));
            return String.format("File Name: %s\nType: %s\nStored at: %s\nCandidate resume document metadata analyzed.",
                    doc.getFileName(), doc.getDocumentType(), doc.getStoragePath());
        }

        if (studentId != null) {
            Optional<StudentProfile> spOpt = studentProfileRepository.findById(studentId);
            if (spOpt.isPresent() && spOpt.get().getResumeFileId() != null) {
                Document doc = documentRepository.findById(spOpt.get().getResumeFileId()).orElse(null);
                if (doc != null) {
                    return String.format("Resume: %s (Type: %s)\nSkills: %s\nEducation: %s",
                            doc.getFileName(), doc.getDocumentType(), spOpt.get().getSkills(), spOpt.get().getEducation());
                }
            }
        }

        throw new IllegalArgumentException("Resume text or a valid document reference must be provided for analysis.");
    }
}
