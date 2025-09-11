package com.example.shopauth.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBasicDto {
    private Long id;
    private String username;
    private String email;
    private String role;
}
