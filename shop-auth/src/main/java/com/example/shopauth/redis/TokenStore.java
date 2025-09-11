package com.example.shopauth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenStore {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "refresh_token:";

    public void store(Long userId, String token, long ttlMs) {
        redisTemplate.opsForValue().set(PREFIX + userId, token, ttlMs, TimeUnit.MILLISECONDS);
    }

    public boolean validate(Long userId, String token) {
        String stored = redisTemplate.opsForValue().get(PREFIX + userId);
        return token.equals(stored);
    }

    public void revoke(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}
