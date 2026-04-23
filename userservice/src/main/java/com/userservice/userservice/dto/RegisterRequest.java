package com.userservice.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "password must be atleast 6 characters ")
    private String password;

    
    private String firstname;
    private String lastname;
    private String keycloakId;

}
