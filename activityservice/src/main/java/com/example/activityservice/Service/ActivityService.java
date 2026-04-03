package com.example.activityservice.Service;

import com.example.activityservice.Model.Activity;
import com.example.activityservice.Repository.ActivityRepository;
import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //creates constructor for only final and non-null fields

public class ActivityService {

    private final ActivityRepository repo;
    private final UserValidationService userValidator;
    private final ModelMapper mapper;


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
