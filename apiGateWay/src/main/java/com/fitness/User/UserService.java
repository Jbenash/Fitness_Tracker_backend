package com.fitness.User;

import com.fitness.apiGateWay.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

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

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        log.info("Calling user Registration API for email: {}", request.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, response -> {
                    log.warn("User already exists with email: {}", request.getEmail());
                    return Mono.error(new RuntimeException("Email already registered"));
                })
                .bodyToMono(UserResponse.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(TimeoutException.class, e -> {
                    log.error("User Service timeout for email: {}", request.getEmail());
                    return Mono.error(new RuntimeException("User Service did not respond in time"));
                });
    }
}
