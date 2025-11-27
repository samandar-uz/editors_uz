package org.example.editors_uz.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class HomeService {
    public void addAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
