package com.fitness.aiservice.Service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public String generateRecommendation (Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse =  geminiService.getAnswer(prompt);
        processAiResponse(activity , aiResponse);
           log.info("Response from Ai {}",aiResponse);

           return aiResponse;
    }
private void processAiResponse (Activity activity,String aiResponse){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode  rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("contents")
                    .path("parts")
                    .get(0)
                    .path("text");


            String jsonContent = textNode.asString()
                            .replaceAll("```json\\n","")
                            .replaceAll("```\\n","")
                            .trim();
            log.info("Parsed response from AI {}" ,jsonContent );

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();

            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace");
            addAnalysisSection(fullAnalysis,analysisNode,"heartRate","HeartRate");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","CaloriesBurned");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));

            log.info("AI analysis for activity {}: {}",
                    activity != null ? activity.getId() : "unknown",
                    fullAnalysis.toString().trim());
            log.info("AI improvements: {}", improvements);
            log.info("AI suggestions: {}", suggestions);

        } catch (Exception e) {
            log.error("Error while processing AI response", e);
        }

}

    private List<String> extractSuggestions(JsonNode suggestionNode) {
        if (suggestionNode == null || !suggestionNode.isArray()) {
            return Collections.singletonList("No specific suggestions provided");
        }

        List<String> suggestions = new ArrayList<>();
        for (JsonNode suggestion : suggestionNode) {
            String workout = suggestion.path("workout").asString();
            String description = suggestion.path("description").asString();

            suggestions.add(String.format("%s %s", workout, description));
        }

        if (suggestions.isEmpty()) {
            return Collections.singletonList("No specific suggestions provided");
        }
        return suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementNode) {
        if (improvementNode == null || !improvementNode.isArray()) {
            return Collections.singletonList("No specific improvements provided");
        }

        List<String> improvements = new ArrayList<>();
        for (JsonNode improvement : improvementNode) {
            String area = improvement.path("area").asString();
            String detail = improvement.path("detail").asString();

            improvements.add(String.format("%s %s", area, detail));
        }

        if (improvements.isEmpty()) {
            return Collections.singletonList("No specific improvements provided");
        }
        return improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asString())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        if (activity == null) {
            return "You are a fitness coach assistant. The activity payload is missing. " +
                    "Return a short message asking for activity details like duration, calories burnt, and intensity.";
        }

        String duration = activity.getDuration() != null ? activity.getDuration() + " minutes" : "not provided";
        String calories = activity.getCaloriesBurnt() != null ? activity.getCaloriesBurnt() + " kcal" : "not provided";
        String additional = activity.getAdditionalMatrice() != null && !activity.getAdditionalMatrice().isEmpty()
                ? activity.getAdditionalMatrice().toString()
                : "not provided";

        return "You are an expert fitness coach. Analyze the user's recent activity and provide concise, practical guidance.\n\n" +
                "Activity details:\n" +
                "- User ID: " + (activity.getUserId() != null ? activity.getUserId() : "unknown") + "\n" +
                "- Duration: " + duration + "\n" +
                "- Calories Burnt: " + calories + "\n" +
                "- Additional Metrics: " + additional + "\n" +
                "- Logged At: " + (activity.getCreatedAt() != null ? activity.getCreatedAt() : "unknown") + "\n\n" +
                "Response format:\n" +
                "1) One-line activity assessment.\n" +
                "2) Three actionable recommendations to improve performance.\n" +
                "3) One safety/recovery suggestion.\n" +
                "Keep response under 120 words and avoid medical diagnosis.";
    }


}