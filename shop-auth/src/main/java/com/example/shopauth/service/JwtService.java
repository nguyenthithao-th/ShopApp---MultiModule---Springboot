// src/main/java/com/example/shopauth/service/JwtService.java
package com.example.shopauth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

import static io.jsonwebtoken.io.Decoders.BASE64;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenMs;
    @Getter
    private final long refreshTokenMs;

    public JwtService(
            @Value("${app.jwt.secret}")  String secret,
            @Value("${app.jwt.access-ms:900000}") long accessTokenMs,
            @Value("${app.jwt.refresh-ms:604800000}") long refreshTokenMs
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured.");
        }
        // tạo chuỗi BASE 64 cho secret, gắn thời gian cho accessToken, refreshToken
        byte[] keyBytes = BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenMs = accessTokenMs;
        this.refreshTokenMs = refreshTokenMs;
    }

    public String generateAccessToken(CustomUserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("email", userDetails.getEmail()); // 👈 thêm email vào claim

        claims.put("id", userDetails.getId()); // 👈 thêm id vào claim

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        claims.put("roles", roles); // 👈 thêm role vào claim

        return buildToken(claims, userDetails.getUsername(), accessTokenMs);
    }

    public Long extractUserId(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    public String extractEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public List<String> extractRoles(String token) {
        // Lấy claim "roles" từ payload trong JWT (có thể là bất cứ kiểu gì → Object)
        Object raw = parseClaims(token).get("roles");

        if (raw instanceof List<?> rawList) {
            return rawList.stream()
                    .map(Object::toString)
                    .toList();
        }

        // Nếu claim "roles" không tồn tại hoặc không phải List → trả về List rỗng
        return Collections.emptyList();
    }



    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(Collections.emptyMap(), userDetails.getUsername(), refreshTokenMs);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long ttlMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(exp.toInstant()))
                .signWith(secretKey, Jwts.SIG.HS256)  // <<< dòng này mới tạo chữ ký !!!
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        Date exp = Date.from(parseClaims(token).getExpiration().toInstant());
        return exp.before(new Date());
    }

    // DÙng để xác thực chữ ký và giải mã payload của JWT token
    private Claims parseClaims(String token) {
        return Jwts.parser()                 // ➊ tạo builder cho JwtParser
                .verifyWith(secretKey)             // ➋ cấu hình khóa bí mật để VERIFY chữ ký
                .build()                           // ➌ build ra JwtParser
                .parseSignedClaims(token)          // ➍ parse + verify token "xxx.yyy.zzz"
                .getPayload();                     // ➎ lấy phần Payload (Claims)
    }


}
