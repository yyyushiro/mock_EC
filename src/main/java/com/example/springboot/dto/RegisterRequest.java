package com.example.springboot.dto;

public record RegisterRequest (
        String email,
        String password,
        String role
) {}
