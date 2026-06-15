package com.example.springboot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductForSellerCreateAndUpdateRequest(
        @NotBlank
        String name,

        @NotNull
        @Min(1)
        Integer price,

        @NotNull
        @Min(0)
        Integer stock
) {}