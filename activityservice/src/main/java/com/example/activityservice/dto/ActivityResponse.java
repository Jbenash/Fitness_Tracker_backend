package com.example.activityservice.dto;

import com.example.activityservice.Model.ActiviyType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponse {
    private String id;
    private String userId;
    private ActiviyType type;
    private Integer duration;
    private Integer caloriesBurnt;
    private Map<String,Object> additionalMatrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
