package com.example.shopauth.dto;

// src/main/java/com/example/shopauth/dto/AuthResponse.java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;
}

