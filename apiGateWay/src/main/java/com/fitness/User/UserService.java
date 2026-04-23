package com.fitness.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j // helps to create a log with manually
public class UserService {

    //webClient is an asynchronous communication
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Validating user with ID: {}", userId);
        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.warn("User not found: {}", userId);
                    return Mono.error(new RuntimeException("User not found"));
                })
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(false);  // Return false on any error (including NOT_FOUND)
    }

}
