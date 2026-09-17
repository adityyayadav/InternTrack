package com.internpilot.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String provider = "groq";
    private String apiKey = "";
    private String model = "llama-3.1-70b-versatile";
    private String baseUrl = "https://api.groq.com/openai/v1";
    private int timeoutSeconds = 45;
}
