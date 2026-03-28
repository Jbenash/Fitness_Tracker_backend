package com.fitness.userservice.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.userRepository;

import lombok.AllArgsConstructor;

//instead of using lots of getters and setters we can use builder() from lompok
// but using model-mapper is even simpler and convenient 

@Service
@AllArgsConstructor
public class userService {

    private final userRepository userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;

    public UserResponse register(RegisterRequest request) {

        // checks for already existing email
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // map request to user
        User user = modelMapper.map(request, User.class);

        // hash the password
        user.setPassword(encoder.encode(user.getPassword()));

        // save user to the database
        User savedUser = userRepo.save(user);

        // Map entity to response
        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);

        return userResponse;

    }

    public UserResponse getUserProfile(String userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return userResponse;

    }

}
