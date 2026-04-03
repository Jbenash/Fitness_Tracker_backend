package com.fitness.aiservice.controller;

import com.fitness.aiservice.Service.recommendationService;
import com.fitness.aiservice.model.recommendations;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class recommendationController {
    private final recommendationService recommendationService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<recommendations>> getUserRecommendations(@PathVariable String userId){
        return ResponseEntity.ok(recommendationService.getUserRecommendations(userId));
    }


    @GetMapping("/activities/{activityId}")
    public ResponseEntity<recommendations> getActivityRecommendations(@PathVariable String activityId){
        return ResponseEntity.ok(recommendationService.getActivityRecommendations(activityId));
    }


}
