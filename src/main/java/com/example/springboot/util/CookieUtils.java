package com.example.springboot.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {

    public static Optional<String> extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue);

    }

    public static Cookie create(String name, String value, boolean httpOnly, int maxAge, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

    public static Cookie delete(String name, boolean secure) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 即時削除
        return cookie;
    }
}
