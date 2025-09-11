// src/main/java/com/example/shopauth/controller/AuthController.java
package com.example.shopauth.controller;

import com.example.shopauth.dto.*;
import com.example.shopauth.redis.TokenStore;
import com.example.shopauth.service.CustomUserDetails;
import com.example.shopauth.service.JwtService;
import com.example.shopauth.service.UserDetailsServiceImpl;
import com.example.shopuser.entity.User;
import com.example.shopuser.repository.UserRepository;
import com.example.shopcore.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenStore tokenStore;

    // ---------------- REGISTER ----------------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Username already exists"));
        }
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists"));
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .role("USER")
                .build();
        userRepository.save(user);

        CustomUserDetails cud = (CustomUserDetails) userDetailsService.loadUserByUsername(user.getUsername());
        String access = jwtService.generateAccessToken(cud);
        String refresh = jwtService.generateRefreshToken(cud);

        tokenStore.store(cud.getId(), refresh, jwtService.getRefreshTokenMs());

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true)                  // JS không đọc được
                .secure(false)                   // dev HTTP = false; production HTTPS = true
                .path("/api/auth/refresh")       // cookie chỉ gửi lên đường dẫn này
                .maxAge(Duration.ofMillis(jwtService.getRefreshTokenMs()))
                .sameSite("Strict")              // giảm nguy cơ CSRF
                .build();


        AuthResponse authResp = new AuthResponse(access, null, cud.getUsername(), user.getRole());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.ok(authResp));
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        try {
            UsernamePasswordAuthenticationToken authReq =
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            authenticationManager.authenticate(authReq);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid credentials"));
        }

        CustomUserDetails cud = (CustomUserDetails) userDetailsService.loadUserByUsername(req.getUsername());
        String access = jwtService.generateAccessToken(cud);
        String refresh = jwtService.generateRefreshToken(cud);

        tokenStore.store(cud.getId(), refresh, jwtService.getRefreshTokenMs());

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true)
                .secure(true) // bật https thì để true
                .path("/api/auth/refresh")
                .maxAge(Duration.ofMillis(jwtService.getRefreshTokenMs()))
                .sameSite("Strict")
                .build();

        AuthResponse authResp = new AuthResponse(access, null, cud.getUsername(), cud.getAuthorities().toString());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.ok(authResp));
    }

    // ---------------- REFRESH ----------------
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Missing refresh token"));
        }
        try {
            String username = jwtService.extractUsername(refreshToken);
            CustomUserDetails cud = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(refreshToken, cud) || !tokenStore.validate(cud.getId(), refreshToken)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Invalid refresh token"));
            }

            String newAccess = jwtService.generateAccessToken(cud);
            String newRefresh = jwtService.generateRefreshToken(cud);

            tokenStore.store(cud.getId(), newRefresh, jwtService.getRefreshTokenMs());

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefresh)
                    .httpOnly(true)
                    .secure(true)
                    .path("/api/auth/refresh")
                    .maxAge(Duration.ofMillis(jwtService.getRefreshTokenMs()))
                    .sameSite("Strict")
                    .build();

            AuthResponse authResp = new AuthResponse(newAccess, null, cud.getUsername(), cud.getAuthorities().toString());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(ApiResponse.ok(authResp));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid refresh token"));
        }
    }

    // ---------------- LOGOUT ----------------
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal CustomUserDetails user) {
        tokenStore.revoke(user.getId());

        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(ApiResponse.ok("Logged out"));
    }

    // ---------------- ME ----------------
//    @GetMapping("/me")
//    public String getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        return "Current user id = " + userDetails.getId();
//    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserBasicDto>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserBasicDto dto = new UserBasicDto(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),   // cần CustomUserDetails có getEmail()
                userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", "")) // trả role gốc
                        .findFirst().orElse("USER")
        );
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }


    // ---------------- ASSIGN ROLE (ADMIN) ----------------
    @PostMapping("/admin/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> assignRole(@Valid @RequestBody AssignRoleRequest req) {
        return userRepository.findByUsernameAndEmail(req.getUsername(), req.getEmail())
                .map(user -> {
                    user.setRole(req.getRole().toUpperCase());
                    userRepository.save(user);
                    return ResponseEntity.ok(ApiResponse.ok(
                            "Assigned role " + req.getRole() + " to user " + req.getUsername()));
                })
                .orElseGet(() -> ResponseEntity
                        .badRequest()
                        .body(ApiResponse.error("User not found with username/email")));
    }
}
