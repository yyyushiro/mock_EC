package com.example.springboot.service;

import com.example.springboot.dto.ProductForSellerCreateAndUpdateRequest;
import com.example.springboot.dto.ProductForSellerResponse;
import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerProductServiceTest {

    @InjectMocks
    SellerProductService sellerProductService;

    @Mock
    ProductRepository productRepository;

    @Test
    void addProduct_shouldPassCorrectProductToRepository() {
        ProductForSellerCreateAndUpdateRequest req =
                new ProductForSellerCreateAndUpdateRequest("test product", 1000, 30);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(productCaptor.capture())).thenReturn(new Product());

        sellerProductService.addProduct(req);

        Product captured = productCaptor.getValue();
        assertThat(captured.getName()).isEqualTo("test product");
        assertThat(captured.getPrice()).isEqualTo(1000);
        assertThat(captured.getStock()).isEqualTo(30);
    }

    @Test
    void addProduct_shouldReturnCorrectResponse() {
        Product savedProduct = new Product();
        savedProduct.setName("test product");
        savedProduct.setPrice(1000);
        savedProduct.setStock(30);

        //何を返すべきか準備
        when(productRepository.save(any())).thenReturn(savedProduct);

        //仮定を置き終えたので実行する
        ProductForSellerResponse response = sellerProductService.addProduct(
                new ProductForSellerCreateAndUpdateRequest("test product", 1000, 30)
        );

        assertThat(response.name()).isEqualTo("test product");
        assertThat(response.price()).isEqualTo(1000);
        assertThat(response.stock()).isEqualTo(30);
    }
}
