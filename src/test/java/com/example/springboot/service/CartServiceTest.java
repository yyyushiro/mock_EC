package com.example.springboot.service;

import com.example.springboot.dto.CartResponse;
import com.example.springboot.entity.CartItem;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.User;
import com.example.springboot.exception.EmptyCartException;
import com.example.springboot.repository.CartItemRepository;
import com.example.springboot.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    CartService cartService;

    @Mock
    UserService userService;

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    ProductRepository productRepository;

    private static final String EMAIL = "test@test.com";

    private User userWithId(long id) {
        return User.builder().id(id).email(EMAIL).hashedPassword("hashed").role("CUSTOMER").build();
    }

    @Test
    void addToCart_newProduct_shouldCreateNewCartItem() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));
        when(productRepository.existsById(any())).thenReturn(true);
        when(cartItemRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any())).thenReturn(new CartItem());

        cartService.addToCart(EMAIL, 10L, 2);

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(captor.capture());
        CartItem saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getProductId()).isEqualTo(10L);
        assertThat(saved.getQuantity()).isEqualTo(2);
    }

    @Test
    void addToCart_existingProduct_shouldIncreaseQuantity() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));
        when(productRepository.existsById(any())).thenReturn(true);

        CartItem existing = CartItem.builder().userId(1L).productId(10L).quantity(3).build();
        when(cartItemRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.of(existing));
        when(cartItemRepository.save(any())).thenReturn(existing);

        cartService.addToCart(EMAIL, 10L, 2);

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(captor.capture());
        assertThat(captor.getValue().getQuantity()).isEqualTo(5);
    }

    @Test
    void addToCart_productNotExist_shouldThrowEntityNotFoundException() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));
        when(productRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> cartService.addToCart(EMAIL, 99L, 1))
                .isInstanceOf(EntityNotFoundException.class);

        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void getCartContents_shouldReturnItemsAndTotalPrice() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));

        CartItem item1 = CartItem.builder().userId(1L).productId(10L).quantity(2).build();
        CartItem item2 = CartItem.builder().userId(1L).productId(20L).quantity(1).build();
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(item1, item2));

        Product p1 = new Product();
        p1.setId(10L);
        p1.setName("Product A");
        p1.setPrice(100);
        p1.setStock(5);

        Product p2 = new Product();
        p2.setId(20L);
        p2.setName("Product B");
        p2.setPrice(200);
        p2.setStock(5);

        when(productRepository.findAllById(List.of(10L, 20L))).thenReturn(List.of(p1, p2));

        CartResponse response = cartService.getCartContents(EMAIL);

        assertThat(response.items()).hasSize(2);
        assertThat(response.totalPrice()).isEqualTo(2 * 100 + 1 * 200);
    }

    @Test
    void getCartContents_emptyCart_shouldThrowEmptyCartException() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> cartService.getCartContents(EMAIL))
                .isInstanceOf(EmptyCartException.class);
    }

    @Test
    void clearCart_shouldDeleteByUserId() {
        when(userService.getByEmail(EMAIL)).thenReturn(userWithId(1L));

        cartService.clearCart(EMAIL);

        verify(cartItemRepository).deleteByUserId(1L);
    }
}
