package com.example.springboot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderResponse(
        @NotNull
        Long id,

        @NotNull
        List<OrderItemResponse> items,

        @Min(1)
        Integer totalPrice
) {}
