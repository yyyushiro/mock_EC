package com.example.springboot.entity;

import java.util.UUID;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    // Replace with uuid v7 in the future.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 50)
    private Long id;

    @Column(name = "uuid_v4", nullable = false, unique = true)
    @Builder.Default
    private UUID uuidV4 = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    @Setter
    private String email;

    @Column(name = "hashed_password", nullable = false)
    @Setter
    private String hashedPassword;

    @Column(nullable = false)
    @Setter
    private String role;
}
