package com.example.springboot.service;

import com.example.springboot.dto.RegisterRequest;
import com.example.springboot.entity.User;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails saveUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with the email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .hashedPassword(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        User savedUser = userRepository.save(user);

        return new UserDetailsImpl(savedUser);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
