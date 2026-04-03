package com.fitness.aiservice.Repository;

import com.fitness.aiservice.model.recommendations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface recommendationRepository extends MongoRepository<recommendations,String> {

    List<recommendations> findByUserId(String userId);

    Optional<recommendations> findByActivityId(String activityId);
}
