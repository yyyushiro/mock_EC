package com.example.springboot.dto;

public record LoginRequest (
        String email,
        String password
) {}
