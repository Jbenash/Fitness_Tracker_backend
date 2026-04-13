package com.fitness.aiservice.Service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public String generateRecommendation (Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse =  geminiService.getAnswer(prompt);
           log.info("Reponse from Ai {}",aiResponse);

           return aiResponse;
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