package com.userservice.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.userservice.userservice.model.User;

@Repository
public interface userRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
}
