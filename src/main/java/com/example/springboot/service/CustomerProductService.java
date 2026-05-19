package com.example.springboot.service;

import com.example.springboot.dto.ProductForCustomer;
import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerProductService {

    private final ProductRepository productRepository;


    public CustomerProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductForCustomer getProduct(long id) {
        Optional<Product> product = productRepository.findById(id);

        return product
                .map(p -> new ProductForCustomer(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                .orElseThrow(() -> new EntityNotFoundException("Item not Found"));
    }

    public List<ProductForCustomer> searchProductsByName(String name) {
        // Avoid the situation that the DB extracts all data.
        if (name == null || name.isBlank()) {
            return List.of();
        }
        List<Product> products = productRepository.findByNameContaining(name);

        return products.stream()
                .map(p -> new ProductForCustomer(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                .toList();
    }

    public List<ProductForCustomer> searchProductsByPrice(Integer minPrice, Integer maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return List.of();
        } else if (minPrice == null) {
            return productRepository.findByPriceLessThanEqual(maxPrice).stream()
                    .map(p -> new ProductForCustomer(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                    .toList();
        } else if (maxPrice == null) {
            return productRepository.findByPriceGreaterThanEqual(minPrice).stream()
                    .map(p -> new ProductForCustomer(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                    .toList();
        } else {
            return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                    .map(p -> new ProductForCustomer(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                    .toList();
        }
    }
}
