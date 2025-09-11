package com.example.shopauth.dto;

// src/main/java/com/example/shopauth/dto/RegisterRequest.java

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @Email private String email;
    private String role; // optional - ADMIN or USER
}
