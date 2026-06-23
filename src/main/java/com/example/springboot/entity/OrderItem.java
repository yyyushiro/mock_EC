package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    // Replace with uuid v7 in the future.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq_gen")
    @SequenceGenerator(name = "order_item_seq_gen", sequenceName = "order_item_seq", allocationSize = 50)
    private Long id;

    @Column(name = "uuid_v4", nullable = false, unique = true)
    @Builder.Default
    private UUID uuidV4 = UUID.randomUUID();

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false)
    private Integer priceAtPurchase;
}
