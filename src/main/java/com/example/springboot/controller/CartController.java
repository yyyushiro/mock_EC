package com.example.springboot.controller;

import com.example.springboot.dto.CartItemRequest;
import com.example.springboot.dto.CartResponse;
import com.example.springboot.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal String email,
                                        @RequestBody @Valid CartItemRequest request) {
        cartService.addToCart(email, request.productId(), request.quantity());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public CartResponse getCartContents(@AuthenticationPrincipal String email) {
        return cartService.getCartContents(email);
    }
}
