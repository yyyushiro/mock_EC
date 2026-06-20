package com.example.springboot.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRE = 60 * 60 * 24 * 7;

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * The given email will be stored in Redis for reissuing an access token,
     * NOT for issuing a refresh token.
     *
     * @param username the given email
     * @return randomly generated refresh token
     */
    public String issueRefreshToken(String username) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refresh:" + token,
                username,
                REFRESH_TOKEN_EXPIRE,
                TimeUnit.SECONDS
        );
        return token;
    }

    public String rotateRefreshToken(String oldToken) {
        String username = findUsernameFromRedis(oldToken).orElse(null);
        if (username == null) return null;

        deleteRefreshToken(oldToken);

        return issueRefreshToken(username);
    }

    /**
     * findUsernameFromRedis validates the given refresh token by finding the same key from Redis.
     *
     *
     * @param token the given refresh token
     * @return Optional string which might be null.
     */
    public Optional<String> findUsernameFromRedis(String token) {
        String saved = redisTemplate.opsForValue().get("refresh:" + token);
        return Optional.ofNullable(saved);
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete("refresh:" + token);
    }
}
