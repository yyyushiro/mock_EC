package com.example.springboot.dto;

public record ProductForCustomer (
    long id,
    String name,
    Integer price,
    Integer stock
) {}
