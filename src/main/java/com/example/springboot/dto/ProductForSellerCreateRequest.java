package com.example.springboot.dto;

public record ProductForSellerCreateRequest(
        String name,
        Integer price,
        Integer stock
) {}