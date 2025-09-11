package com.example.shopauth.dto;

// src/main/java/com/example/shopauth/dto/RefreshRequest.java

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {
    @NotBlank private String refreshToken;
}
