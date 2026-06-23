package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_item_seq_gen"
    )
    @SequenceGenerator(
            name = "cart_item_seq_gen",
            sequenceName = "cart_item_seq",
            allocationSize = 50)
    private Long id;

    @Column(name = "uuid_v4", nullable = false, unique = true)
    @Builder.Default
    private UUID uuidV4 = UUID.randomUUID();

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    @Setter
    private Integer quantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
