package com.fitness.aiservice.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.Repository.recommendationRepository;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.recommendations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;
    private final recommendationRepository recommendationRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateRecommendation(Activity activity) {
        log.info("Generating AI recommendation for activity: {} (Type: {})", activity.getId(), activity.getType());
        String prompt = createPromptForActivity(activity);

        try {
            String aiResponse = geminiService.getAnswer(prompt);
            processAiResponse(activity, aiResponse);
            log.info("Successfully processed and saved AI recommendations for activity: {}", activity.getId());
            return aiResponse;
        } catch (Exception e) {
            log.error("Failed to generate or process AI recommendation for activity {}", activity.getId(), e);
            throw new RuntimeException("Failed to generate recommendation for activity " + activity.getId(), e);
        }
    }

    private void processAiResponse(Activity activity, String aiResponse) throws Exception {
        JsonNode rootNode = objectMapper.readTree(aiResponse);
        
        if (rootNode.has("error")) {
            throw new RuntimeException("AI service returned error: " + rootNode.path("error").asText());
        }

        // Navigate to the actual text content from Gemini's response structure
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
            throw new RuntimeException("No candidates found in AI response");
        }

        JsonNode firstCandidate = candidates.get(0);
        JsonNode parts = firstCandidate.path("content").path("parts");
        if (!parts.isArray() || parts.isEmpty()) {
            throw new RuntimeException("AI response parts are missing");
        }

        JsonNode textNode = parts.get(0).path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            throw new RuntimeException("AI response text is missing in parts");
        }

        String jsonContent = textNode.asText()
                .replaceAll("(?i)```json", "")
                .replace("```", "")
                .trim();

        JsonNode analysisJson = objectMapper.readTree(jsonContent);

        // Map AI response to our recommendations model
        recommendations rec = recommendations.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType() != null ? activity.getType() : "Workout")
                .recommendationText(analysisJson.path("summary").asText("No summary provided"))
                .improvements(extractList(analysisJson.path("improvements")))
                .suggestions(extractList(analysisJson.path("suggestions")))
                .safety(extractList(analysisJson.path("safety")))
                .createdAt(LocalDateTime.now())
                .build();

        // SAVE TO DATABASE
        recommendationRepo.save(rec);
    }


    private List<String> extractList(JsonNode node) {
        if (node == null || !node.isArray()) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (JsonNode item : node) {
            list.add(item.asText());
        }
        return list;
    }

    private String createPromptForActivity(Activity activity) {
        String type = activity.getType() != null ? activity.getType() : "Workout";
        return "You are an expert fitness coach. Analyze the user's recent " + type + " activity and provide specific, actionable insights in JSON format.\n\n" +
                "Activity Details:\n" +
                "- Type: " + type + "\n" +
                "- Duration: " + activity.getDuration() + " minutes\n" +
                "- Calories Burnt: " + activity.getCaloriesBurnt() + " kcal\n" +
                "- Date: " + activity.getCreatedAt() + "\n\n" +
                "Return ONLY a JSON object with this exact structure:\n" +
                "{\n" +
                "  \"summary\": \"A short 1-sentence assessment of the workout.\",\n" +
                "  \"improvements\": [\"3 specific bullet points on how to improve next time\"],\n" +
                "  \"suggestions\": [\"2 workout variations or next-step exercises\"],\n" +
                "  \"safety\": [\"1 safety or recovery tip based on intensity\"]\n" +
                "}\n" +
                "Do not include any text outside the JSON block.";
    }
}