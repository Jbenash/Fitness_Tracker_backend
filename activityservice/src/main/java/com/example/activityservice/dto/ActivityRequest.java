package com.example.activityservice.dto;

import com.example.activityservice.Model.ActiviyType;
import lombok.Data;

import java.util.Map;

@Data
public class ActivityRequest {
    private String userId;
    private ActiviyType type;
    private Integer duration;
    private Integer caloriesBurnt;
    private Map<String,Object> additionalMatrice;
}
