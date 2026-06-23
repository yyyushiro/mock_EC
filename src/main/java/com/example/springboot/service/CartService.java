package com.example.springboot.service;

import com.example.springboot.dto.CartItemResponse;
import com.example.springboot.dto.CartResponse;
import com.example.springboot.entity.CartItem;
import com.example.springboot.entity.Product;
import com.example.springboot.exception.EmptyCartException;
import com.example.springboot.repository.CartItemRepository;
import com.example.springboot.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserService userService;

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public void addToCart(String email, Long productId, Integer quantity) {
        long userId = userService.getByEmail(email).getId();

        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found");
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .userId(userId)
                        .productId(productId)
                        .quantity(quantity)
                        .build());

        cartItemRepository.save(item);
    }

    public CartResponse getCartContents(String email) {
        long userId = userService.getByEmail(email).getId();

        List<CartItem> items = getCartItems(userId);

        List<Long> productIds = items.stream()
                .map(CartItem::getProductId)
                .toList();

        Map<Long, Product> productsById = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemResponse> cartItems = items.stream()
                .map(item -> {
                    Product p = productsById.get(item.getProductId());
                    if (p == null) {
                        throw new EntityNotFoundException("Product no longer exists: " + item.getProductId());
                    }
                    return new CartItemResponse(p.getId(), p.getName(), p.getPrice(), item.getQuantity());
                })
                .toList();

        int totalPrice = items.stream()
                .mapToInt(item -> productsById.get(item.getProductId()).getPrice() * item.getQuantity())
                .sum();

        return new CartResponse(cartItems, totalPrice);
    }

    public List<CartItem> getCartItems(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);

        if (items.isEmpty()) {
            throw new EmptyCartException("No item found in cart");
        }

        return items;
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public void clearCart(String email) {
        clearCart(userService.getByEmail(email).getId());
    }
}
