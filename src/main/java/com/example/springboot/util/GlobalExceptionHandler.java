package com.example.springboot.util;

import com.example.springboot.exception.EmptyCartException;
import com.example.springboot.exception.InsufficientStockException;
import com.example.springboot.exception.InvalidRefreshTokenException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleInvalidRefreshToken() {
        return ResponseEntity.status(401).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument() {
        return ResponseEntity.status(400).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound() {
        return ResponseEntity.status(404).build();
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStock() {
        // 409 means the contradiction between the request and the current status.
        return ResponseEntity.status(409).build();
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<?> handleEmptyCart() {
        return ResponseEntity.status(400).build();
    }

    // Place this method at the bottom of this class.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected() {
        return ResponseEntity.status(500).build();
    }
}
