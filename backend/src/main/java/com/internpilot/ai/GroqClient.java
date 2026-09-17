package com.internpilot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroqClient {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Executes a chat completion call to Groq's OpenAI-compatible API.
     * Expects a JSON object back when jsonMode is true.
     */
    public String chatCompletion(String systemPrompt, String userPrompt, boolean jsonMode) {
        String apiKey = aiProperties.getApiKey();

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI_API_KEY is not configured. Utilizing deterministic AI simulation fallback.");
            return generateSimulationResponse(systemPrompt, userPrompt);
        }

        try {
            String baseUrl = aiProperties.getBaseUrl().replaceAll("/+$", "");
            String endpoint = baseUrl + "/chat/completions";

            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", aiProperties.getModel());
            root.put("temperature", 0.2);

            if (jsonMode) {
                ObjectNode responseFormat = root.putObject("response_format");
                responseFormat.put("type", "json_object");
            }

            ArrayNode messages = root.putArray("messages");
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                ObjectNode sysMsg = messages.addObject();
                sysMsg.put("role", "system");
                sysMsg.put("content", systemPrompt);
            }

            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userPrompt);

            String requestJson = objectMapper.writeValueAsString(root);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() == 429) {
                log.error("Groq rate limit exceeded (HTTP 429)");
                throw new IllegalStateException("AI service rate limit reached. Please try again shortly.");
            }

            if (response.statusCode() >= 300) {
                log.error("Groq API error {}: {}", response.statusCode(), response.body());
                throw new IllegalStateException("AI service returned error: " + response.statusCode());
            }

            JsonNode responseJson = objectMapper.readTree(response.body());
            JsonNode choices = responseJson.get("choices");
            if (choices != null && choices.isArray() && !choices.isEmpty()) {
                JsonNode message = choices.get(0).get("message");
                if (message != null && message.has("content")) {
                    return message.get("content").asText();
                }
            }

            throw new IllegalStateException("Empty response received from AI model.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AI request interrupted.", e);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to communicate with Groq AI API: {}", e.getMessage(), e);
            log.warn("Falling back to simulated AI response due to communication failure.");
            return generateSimulationResponse(systemPrompt, userPrompt);
        }
    }

    /**
     * Deterministic simulation fallback when AI_API_KEY is not set or network call fails.
     */
    private String generateSimulationResponse(String systemPrompt, String userPrompt) {
        String promptLower = (systemPrompt + " " + userPrompt).toLowerCase();

        if (promptLower.contains("resume") || promptLower.contains("skills")) {
            return """
            {
              "skills": ["Java", "Spring Boot", "PostgreSQL", "REST APIs", "Docker", "Git"],
              "technologies": ["Spring Boot 3", "PostgreSQL", "React", "TypeScript", "Tailwind CSS", "Maven"],
              "education": [
                {
                  "institution": "University School of Information Technology",
                  "degree": "Bachelor of Technology",
                  "fieldOfStudy": "Computer Science and Engineering",
                  "graduationYear": "2025",
                  "gpa": "3.8/4.0"
                }
              ],
              "experience": [
                {
                  "organization": "Open Source Contributor",
                  "role": "Software Engineering Intern",
                  "duration": "6 Months",
                  "description": "Developed REST microservices using Java and Spring Boot with PostgreSQL storage."
                }
              ],
              "summary": "Proficient software engineer with practical experience in backend Java/Spring Boot development, relational databases, and modern cloud deployment architectures."
            }
            """;
        } else if (promptLower.contains("match") || promptLower.contains("opportunity")) {
            return """
            {
              "matches": [
                {
                  "matchPercentage": 88,
                  "matchLevel": "HIGH",
                  "matchReason": "Strong alignment in core backend Java technologies, Spring Boot, and relational database schema design.",
                  "matchedSkills": ["Java", "Spring Boot", "PostgreSQL", "REST APIs"],
                  "missingSkills": ["AWS Cloud Deployment"]
                }
              ]
            }
            """;
        } else {
            // Weekly report fallback
            return """
            {
              "executiveSummary": "Student made substantial progress completing sprint objectives, building backend endpoints, and integrating database migrations.",
              "keyAchievements": [
                "Implemented secure REST controllers and service contracts",
                "Successfully executed Flyway database migrations without data loss",
                "Wired role-based authorization filters"
              ],
              "blockersIdentified": [
                "Awaiting third-party staging environment credentials"
              ],
              "riskAssessment": "LOW",
              "recommendedActions": [
                "Coordinate with mentor for third-party service credential provisioning",
                "Add integration test coverage for newly created controller endpoints"
              ]
            }
            """;
        }
    }
}
