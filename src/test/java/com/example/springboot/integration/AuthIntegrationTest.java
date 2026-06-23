package com.example.springboot.integration;

import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void registerLoginRefreshLogout_shouldSucceedEndToEnd() throws Exception {
        RegisterRequest registerRequest =
                new RegisterRequest("integration-test@example.com", "password123", "CUSTOMER");

        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                //Java Object を JSON文字列に変換することで、実際のHTTPリクエストを再現している。
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"));

        LoginRequest loginRequest =
                new LoginRequest("integration-test@example.com", "password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        String refreshToken = loginResult.getResponse().getCookie("refresh_token").getValue();
        Cookie csrfCookie = loginResult.getResponse().getCookie("XSRF-TOKEN");
        String csrfToken = loginResult.getResponse().getCookie("XSRF-TOKEN").getValue();


        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                .cookie(loginResult.getResponse().getCookies())
                .header("X-XSRF-TOKEN", csrfToken))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        String newRefreshToken = refreshResult.getResponse().getCookie("refresh_token").getValue();

        assertNotEquals(refreshToken, newRefreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .cookie(csrfCookie)
                .cookie(refreshResult.getResponse().getCookie("access_token"))
                .cookie(refreshResult.getResponse().getCookie("refresh_token"))
                .header("X-XSRF-TOKEN", csrfToken))
                .andExpect(status().isOk());
    }
}
