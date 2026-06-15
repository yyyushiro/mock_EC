package com.example.springboot.dto;

import java.util.UUID;

public record ProductForSellerResponse(
        UUID uuidV4,
        String name,
        Integer price,
        Integer stock
) {}
