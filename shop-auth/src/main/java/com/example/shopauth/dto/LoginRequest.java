package com.example.shopauth.dto;

// src/main/java/com/example/shopauth/dto/LoginRequest.java


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank private String username;
    @NotBlank private String password;
}
