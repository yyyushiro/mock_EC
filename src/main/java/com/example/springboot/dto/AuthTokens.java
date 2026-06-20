package com.example.springboot.dto;

public record AuthTokens (
        String accessToken,
        String refreshToken
) {}
