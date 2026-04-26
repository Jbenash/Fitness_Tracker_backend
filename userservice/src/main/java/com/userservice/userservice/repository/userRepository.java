package com.userservice.userservice.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.userservice.userservice.model.User;

@Repository
public interface userRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    User findByEmail(@NotBlank(message = "Email cannot be blank") @Email String email);

    boolean existsByKeycloakId(String keycloakId);

    User findByKeycloakId(String keycloakId);
}
