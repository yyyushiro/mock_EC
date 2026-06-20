package com.example.springboot.controller;

import com.example.springboot.dto.AuthTokens;
import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.RegisterRequest;
import com.example.springboot.service.AuthService;
import com.example.springboot.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletResponse response) {

        AuthTokens authTokens = authService.login(request);

        response.addCookie(CookieUtils.create("access_token", authTokens.accessToken(), true, 60 * 15));
        response.addCookie(CookieUtils.create("refresh_token", authTokens.refreshToken(), true, 60 * 60 * 24 * 7));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = true) String refreshToken,
                                     HttpServletResponse response) {
        AuthTokens authTokens = authService.refresh(refreshToken);

        response.addCookie(CookieUtils.create("access_token", authTokens.accessToken(), true, 60 * 15));
        response.addCookie(CookieUtils.create("refresh_token", authTokens.refreshToken(), true, 60 * 60 * 24 * 7));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = true) String refreshToken,
                                    HttpServletResponse response) {
        // delete two cookies
        authService.logout(refreshToken);

        response.addCookie(CookieUtils.delete("access_token"));
        response.addCookie(CookieUtils.delete("refresh_token"));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request,
                                      HttpServletResponse response) {
        AuthTokens authTokens = authService.register(request);

        response.addCookie(CookieUtils.create("access_token", authTokens.accessToken(), true, 60 * 15));
        response.addCookie(CookieUtils.create("refresh_token", authTokens.refreshToken(), true, 60 * 60 * 24 * 7));



        return ResponseEntity.ok().build();
    }
}
