package com.example.springboot.repository;

import com.example.springboot.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContaining(String name);

    Optional<Product> findByUuidV4(UUID uuidV4);

    List<Product> findByPriceLessThanEqual(int price);

    List<Product> findByPriceGreaterThanEqual(int price);

    List<Product> findByPriceBetween(int minPrice, int maxPrice);

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.uuidV4 = ?1")
    void deleteByUuidV4(UUID uuidV4);
}
