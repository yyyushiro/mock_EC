package com.example.springboot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartResponse (
        @NotNull
        List<CartItemResponse> items,

        @Min(1)
        Integer totalPrice
) {}
