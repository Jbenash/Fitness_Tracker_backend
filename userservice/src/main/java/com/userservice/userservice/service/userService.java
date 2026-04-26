package com.userservice.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.userservice.userservice.dto.UserResponse;
import com.userservice.userservice.dto.RegisterRequest;
import com.userservice.userservice.model.User;
import com.userservice.userservice.repository.userRepository;

import lombok.AllArgsConstructor;

//instead of using lots of getters and setters we can use builder() from lompok
// but using model-mapper is even simpler and convenient 

@Service
@AllArgsConstructor
@Slf4j
public class userService {

    private final userRepository userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;

    public UserResponse register(RegisterRequest request) {

        // checks for already existing email
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        // map request to user
        User user = modelMapper.map(request, User.class);

        // hash the password
        user.setPassword(encoder.encode(request.getPassword()));

        // save user to the database
        User savedUser = userRepo.save(user);

        // Map entity and return it as a response
        return  modelMapper.map(savedUser, UserResponse.class);

    }

    public UserResponse getUserProfile(String userId) {

        User user = userRepo.findByKeycloakId(userId);
        if (user == null) {
            throw new RuntimeException("User not found with Keycloak ID: " + userId);
        }

        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return userResponse;

    }

    public Boolean existById(String userId) {
            log.info("Checking existence of user with Keycloak ID: {}", userId);
        return userRepo.existsByKeycloakId(userId);
    }
}
