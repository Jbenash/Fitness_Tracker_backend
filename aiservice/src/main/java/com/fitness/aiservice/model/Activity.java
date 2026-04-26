package com.fitness.aiservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {

    private String id;
    private String userId;
    private String type;
    private Integer duration;
    private Integer caloriesBurnt;
    private Map<String,Object> additionalMatrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
