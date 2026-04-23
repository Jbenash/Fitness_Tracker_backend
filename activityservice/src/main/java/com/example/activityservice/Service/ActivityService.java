package com.example.activityservice.Service;

import com.example.activityservice.Model.Activity;
import com.example.activityservice.Repository.ActivityRepository;
import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //creates constructor for only final and non-null fields
@Slf4j
public class ActivityService {

    private final ActivityRepository repo;
    private final UserValidationService userValidator;
    private final ModelMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}") //fetching the values from property file
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValid = Boolean.TRUE.equals(userValidator.validateUser(request.getUserId())
                .timeout(Duration.ofSeconds(3))
                .onErrorReturn(false)
                .block());

        if (!isValid) {
            throw new RuntimeException("Invalid User : " + request.getUserId());
        }
        Activity activity = mapper.map(request, Activity.class);

        Activity savedActivity = repo.save(activity);

        ActivityResponse response = mapper.map(savedActivity, ActivityResponse.class); //even though we are redundantly mapping twice  , but for reusability purpose we are going with this method

        //publish to rabbitmq for AI processing -rabbitmq is a synchronous communication
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, response);
        } catch (AmqpException e) {
            // Keep API success independent of transient broker failures.
            log.error("Failed to publish activity {} for user {} to RabbitMQ", savedActivity.getId(), savedActivity.getUserId(), e);
        }
        return response;
    }

    public List<ActivityResponse> getAllActivities(String id) {

        List<Activity> activities = repo.findByUserId(id);

        return activities.stream()
                .map(activity -> mapper.map(activity,ActivityResponse.class))
                .collect(Collectors.toList());


    }


    public ActivityResponse getActivity(String id) {
     return repo.findById(id)
             .map(activity -> mapper.map(activity,ActivityResponse.class))
             .orElseThrow(()->new RuntimeException("Activity Not found with id: "+ id));


    }
}
