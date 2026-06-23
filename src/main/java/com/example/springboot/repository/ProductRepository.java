package com.example.springboot.repository;

import com.example.springboot.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContaining(String name);

    Optional<Product> findByUuidV4(UUID uuidV4);

    List<Product> findByPriceLessThanEqual(int price);

    List<Product> findByPriceGreaterThanEqual(int price);

    List<Product> findByPriceBetween(int minPrice, int maxPrice);

    boolean existsById(long id);

    void deleteByUuidV4(UUID uuidV4);
}
