package com.example.springboot.util;

import com.example.springboot.exception.InvalidRefreshTokenException;
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
    
    // Place this method at the bottom of this class.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected() {
        return ResponseEntity.status(500).build();
    }
}
