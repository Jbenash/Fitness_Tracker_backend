package com.fitness.aiservice.controller;

import com.fitness.aiservice.Service.recommendationService;
import com.fitness.aiservice.model.recommendations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@Slf4j
public class recommendationController {
    private final recommendationService service;


    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserRecommendations(@PathVariable String userId){
        try {
            log.info("Fetching recommendations for user: {}", userId);
            return ResponseEntity.ok(service.getUserRecommendations(userId));
        } catch (Exception e) {
            log.error("Error fetching recommendations for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Error fetching recommendations: " + e.getMessage());
        }
    }


    @GetMapping("/activities/{activityId}")
    public ResponseEntity<?> getActivityRecommendations(@PathVariable String activityId){
        try {
            log.info("Fetching recommendations for activity: {}", activityId);
            Optional<recommendations> rec = service.getActivityRecommendations(activityId);
            if (rec.isPresent()) {
                return ResponseEntity.ok(rec.get());
            } else {
                log.warn("Recommendation not found for activity: {}", activityId);
                return ResponseEntity.accepted().body(Map.of(
                        "activityId", activityId,
                        "status", "pending",
                        "message", "Recommendation not yet generated for this activity. Please retry shortly."
                ));
            }
        } catch (Exception e) {
            log.error("Error fetching recommendations for activity {}: {}", activityId, e.getMessage());
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }


}
