package com.example.springboot.service;

import com.example.springboot.dto.ProductForCustomer;
import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductForCustomer getProductForCustomer(long id) {
        Optional<Product> product = productRepository.findById(id);

        return product
                .map(p -> new ProductForCustomer(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                .orElseThrow(() -> new EntityNotFoundException("Item not Found"));
    }
}
