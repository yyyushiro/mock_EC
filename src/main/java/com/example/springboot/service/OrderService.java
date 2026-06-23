package com.example.springboot.service;

import com.example.springboot.dto.OrderItemResponse;
import com.example.springboot.dto.OrderResponse;
import com.example.springboot.entity.CartItem;
import com.example.springboot.entity.Order;
import com.example.springboot.entity.OrderItem;
import com.example.springboot.entity.Product;
import com.example.springboot.exception.InsufficientStockException;
import com.example.springboot.repository.OrderItemRepository;
import com.example.springboot.repository.OrderRepository;
import com.example.springboot.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final CartService cartService;

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    @Transactional
    public OrderResponse checkout(String email) {
        long userId = userService.getByEmail(email).getId();

        List<CartItem> cartItems = cartService.getCartItems(userId);

        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .toList();

        Map<Long, Product> productsById = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        int totalPrice = 0;
        // check it is out of stock or not.
        for (CartItem cartItem : cartItems) {
            Product p = productsById.get(cartItem.getProductId());
            if (p == null) {
                throw new EntityNotFoundException("Product no longer exists: " + cartItem.getProductId());
            }
            if (cartItem.getQuantity() > p.getStock()) {
                throw new InsufficientStockException("You put " + cartItem.getQuantity() + " " + p.getName() + ", but there are only " + p.getStock());
            }
            p.setStock(p.getStock() - cartItem.getQuantity());
            totalPrice += p.getPrice() * cartItem.getQuantity();
        }

        Order order = Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .build();

        final Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    Product p = productsById.get(cartItem.getProductId());
                    return OrderItem.builder()
                            .orderId(savedOrder.getId())
                            .productId(cartItem.getProductId())
                            .quantity(cartItem.getQuantity())
                            .priceAtPurchase(p.getPrice())
                            .build();
                })
                .toList();

        orderItemRepository.saveAll(orderItems);

        cartService.clearCart(userId);

        List<OrderItemResponse> orderItemResponses = orderItems.stream()
                .map(orderItem -> {
                    Product p = productsById.get(orderItem.getProductId());
                    return new OrderItemResponse(p.getId(), p.getName(), orderItem.getPriceAtPurchase(), orderItem.getQuantity());
                })
                .toList();

        return new OrderResponse(savedOrder.getId(), orderItemResponses, savedOrder.getTotalPrice());
    }
}
