package com.example.springboot.controller;

import com.example.springboot.dto.OrderResponse;
import com.example.springboot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse checkout(@AuthenticationPrincipal String email) {
        return orderService.checkout(email);
    }
}
