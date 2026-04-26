package com.fitness.aiservice.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url:}")
    private String geminiApiUrl;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String question) {
        if (geminiApiUrl == null || geminiApiUrl.isBlank() || geminiApiKey == null || geminiApiKey.isBlank()) {
            log.warn("Gemini configuration is missing. Set gemini.api.url and gemini.api.key (or GEMINI_API_URL/GEMINI_API_KEY).");
            return "{\"error\":\"Gemini configuration missing\"}";
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", question)
                        ))
                )
        );

        return webClient.post()
                .uri(geminiApiUrl.trim())
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", geminiApiKey.trim())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional()
                .orElse("{}");
    }
}
