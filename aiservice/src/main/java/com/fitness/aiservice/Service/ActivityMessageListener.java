package com.fitness.aiservice.Service;

import com.fitness.aiservice.model.Activity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

private final ActivityAiService aiService;
private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.name:activity.queue}")
    public void processActivity(Map<String, Object> activityPayload){
        Activity activity = objectMapper.convertValue(activityPayload, Activity.class);
        log.info("Received activity message: {}", activity.getId());
        aiService.generateRecommendation(activity);
    }
}
