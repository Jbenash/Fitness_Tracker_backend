package com.example.activityservice.Controller;

import com.example.activityservice.Service.ActivityService;
import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import com.example.activityservice.dto.ActivityUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor //creates constructor for all fields
@RequestMapping("/api/activities")
public class activityController {

    private final ActivityService activityService;


    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivities(@RequestHeader("X-User-Id") String id) {
        return ResponseEntity.ok(activityService.getAllActivities(id));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId) {
        return ResponseEntity.ok(activityService.getActivity(activityId));
    }


    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String activityId) {

        activityService.deleteActivity(activityId);
        return ResponseEntity.noContent().build();// 204 No Content

    }

    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> updateActivity(@PathVariable String activityId,
                                                           @RequestBody ActivityUpdateRequest activityUpdateRequest) {
        return ResponseEntity.ok(activityService.updateActivity(activityId, activityUpdateRequest));
    }


}