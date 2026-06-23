package com.example.springboot.repository;

import com.example.springboot.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    Optional<Order> findByUuidV4(UUID uuidV4);
}