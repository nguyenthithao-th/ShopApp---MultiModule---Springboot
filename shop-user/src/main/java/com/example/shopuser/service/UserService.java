package com.example.shopuser.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopcore.exception.BusinessException;
import com.example.shopuser.dto.UserDto;
import com.example.shopuser.entity.User;
import com.example.shopuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    public ApiResponse<UserDto> createUser(UserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Email đã tồn tại");
        }
        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword()) // TODO: mã hoá password
                .email(dto.getEmail())
                .role(dto.getRole() != null ? dto.getRole() : "USER")
                .build();
        userRepository.save(user);
        dto.setId(user.getId());
        return ApiResponse.ok(dto);
    }

    public ApiResponse<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.ok(users);
    }

    public ApiResponse<UserDto> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User không tồn tại"));
        return ApiResponse.ok(UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }

    public ApiResponse<Void> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("User không tồn tại");
        }
        userRepository.deleteById(id);
        return ApiResponse.ok(null);
    }
}
