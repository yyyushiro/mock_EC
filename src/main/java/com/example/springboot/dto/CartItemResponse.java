package com.example.springboot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartItemResponse(
    @NotNull
    Long productId,

    @NotBlank
    String productName,

    @Min(1)
    Integer price,

    @Min(1)
    Integer quantity
){
}
