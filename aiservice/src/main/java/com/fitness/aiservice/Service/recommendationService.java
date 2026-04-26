package com.fitness.aiservice.Service;

import com.fitness.aiservice.Repository.recommendationRepository;
import com.fitness.aiservice.model.recommendations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class recommendationService {
    private final recommendationRepository repo;

    public List<recommendations> getUserRecommendations(String userId) {
        return repo.findByUserId(userId);
    }

    public java.util.Optional<recommendations> getActivityRecommendations(String activityId) {
        return repo.findByActivityId(activityId);
    }
}
