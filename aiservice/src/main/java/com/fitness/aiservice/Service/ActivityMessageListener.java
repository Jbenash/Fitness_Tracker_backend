package com.fitness.aiservice.Service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

private final ActivityAiService aiService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void  processActivity(Activity activity){
        //here jacksonMessage will automatically populate the incomming activity message to the activity object
        //you just need to create the function and the relevant parameter that is needed to be populated
        log.info("Received activity message: {}", activity.getId());
        log.info("Generated Recommendations {}" ,aiService.generateRecommendation(activity));
    }
}
