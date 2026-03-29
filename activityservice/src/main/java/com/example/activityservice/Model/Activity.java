package com.example.activityservice.Model;

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

@Document(collection = "activities")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Activity {
    @Id
    private String id;
    private String userId;
    private ActiviyType type;
    private Integer duration;
    private Integer caloriesBurnt;

    @Field("metrics")
    private Map<String,Object> additionalMatrice;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
