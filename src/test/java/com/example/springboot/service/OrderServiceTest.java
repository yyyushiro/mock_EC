package com.example.springboot.service;

import com.example.springboot.dto.OrderResponse;
import com.example.springboot.entity.CartItem;
import com.example.springboot.entity.Order;
import com.example.springboot.entity.OrderItem;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.User;
import com.example.springboot.exception.EmptyCartException;
import com.example.springboot.exception.InsufficientStockException;
import com.example.springboot.repository.OrderItemRepository;
import com.example.springboot.repository.OrderRepository;
import com.example.springboot.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    UserService userService;

    @Mock
    CartService cartService;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    private static final String EMAIL = "test@test.com";

    private User userWithId(long id) {
        return User.builder().id(id).email(EMAIL).hashedPassword("hashed").role("CUSTOMER").build();
    }

    private Product product(long id, String name, int price, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        return p;
    }

    @Test
    void checkout_sufficientStock_shouldCreateOrderAndOrderItems() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));

        CartItem cartItem = CartItem.builder().userId(1L).productId(10L).quantity(2).build();
        when(cartService.getCartItems(1L)).thenReturn(List.of(cartItem));

        Product product = product(10L, "Product A", 100, 5);
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(product));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order arg = invocation.getArgument(0);
            return Order.builder()
                    .id(99L)
                    .userId(arg.getUserId())
                    .totalPrice(arg.getTotalPrice())
                    .build();
        });

        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.checkout(EMAIL);

        // 在庫が正しく減算されていること
        assertThat(product.getStock()).isEqualTo(3);

        // OrderResponseの内容が正しいこと
        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.totalPrice()).isEqualTo(200);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
        assertThat(response.items().get(0).priceAtPurchase()).isEqualTo(100);

        // カートがクリアされていること
        verify(cartService).clearCart(1L);

        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepository).saveAll(captor.capture());
        assertThat(captor.getValue().get(0).getOrderId()).isEqualTo(99L);
    }

    @Test
    void checkout_insufficientStock_shouldThrowInsufficientStockExceptionAndNotPersistAnything() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));

        CartItem cartItem = CartItem.builder().userId(1L).productId(10L).quantity(5).build();
        when(cartService.getCartItems(1L)).thenReturn(List.of(cartItem));

        Product product = product(10L, "Product A", 100, 1); // 在庫1しかないのに5個欲しい
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(product));

        assertThatThrownBy(() -> orderService.checkout(EMAIL))
                .isInstanceOf(InsufficientStockException.class);

        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
        verify(cartService, never()).clearCart(anyLong());
    }

    @Test
    void checkout_productNoLongerExists_shouldThrowEntityNotFoundException() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));

        CartItem cartItem = CartItem.builder().userId(1L).productId(10L).quantity(1).build();
        when(cartService.getCartItems(1L)).thenReturn(List.of(cartItem));

        // 商品が削除済みなので findAllById は空リストを返す
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.checkout(EMAIL))
                .isInstanceOf(EntityNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void checkout_emptyCart_shouldPropagateEmptyCartException() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));
        when(cartService.getCartItems(1L)).thenThrow(new EmptyCartException("No item found in cart"));

        assertThatThrownBy(() -> orderService.checkout(EMAIL))
                .isInstanceOf(EmptyCartException.class);

        verify(orderRepository, never()).save(any());
    }
}
