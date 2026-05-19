package com.example.springboot.repository;

import com.example.springboot.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findByNameContaining(String name);

    public List<Product> findByPriceLessThanEqual(int price);

    public List<Product> findByPriceGreaterThanEqual(int price);

    public List<Product> findByPriceBetween(int minPrice, int maxPrice);
}
