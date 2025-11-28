package org.example.editors_uz.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.UserRepository;
import org.example.editors_uz.service.HomeService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final HomeService homeService;
    private final BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/auth")
    public String handleLoginOrRegister(
            @Valid @ModelAttribute AuthForm authForm,
            BindingResult bindingResult,
            HttpServletResponse response,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "❌ Ma'lumotlar noto'g'ri kiritildi!");
            return "auth";
        }

        try {
            User user = userRepository.findByUsername(authForm.getUsername()).orElse(null);

            if (user != null) {
                return handleLogin(user, authForm.getPassword(), response, model);
            } else {
                return handleRegister(authForm, response);
            }

        } catch (Exception e) {
            log.error("Authentication error for user: {}, error: {}",
                    authForm.getUsername(), e.getMessage());
            model.addAttribute("errorMessage", "❌ Tizimda xatolik yuz berdi. Qaytadan urinib ko'ring.");
            return "auth";
        }
    }

    private String handleLogin(User user, String password, HttpServletResponse response, Model model) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Failed login attempt for user: {}", user.getUsername());
            model.addAttribute("errorMessage", "❌ Parol noto'g'ri!");
            return "auth";
        }

        if (!user.isEnabled()) {
            log.warn("Login attempt for disabled user: {}", user.getUsername());
            model.addAttribute("errorMessage", "🚫 Profil bloklangan!");
            return "auth";
        }

        String token = homeService.generateToken();
        user.setKey(token);
        userRepository.save(user);
        homeService.addAuthCookie(response, token);

        log.info("User logged in: {}", user.getUsername());
        return "redirect:/index";
    }

    private String handleRegister(AuthForm authForm, HttpServletResponse response) {
        if (userRepository.findByUsername(authForm.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        String token = homeService.generateToken();
        User newUser = User.builder()
                .username(authForm.getUsername())
                .password(passwordEncoder.encode(authForm.getPassword()))
                .key(token)
                .enabled(true)
                .role("USER")
                .salary(0)
                .build();

        userRepository.save(newUser);
        homeService.addAuthCookie(response, token);

        log.info("New user registered: {}", newUser.getUsername());
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response,
                         @CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        if (token != null) {
            userRepository.findByKey(token).ifPresent(user -> {
                user.setKey(null);
                userRepository.save(user);
                log.info("User logged out: {}", user.getUsername());
            });
        }

        homeService.removeAuthCookie(response);
        return "redirect:/auth";
    }

    @Data
    public static class AuthForm {
        @NotBlank(message = "Username bo'sh bo'lmasligi kerak")
        @Size(min = 3, max = 50, message = "Username 3-50 belgi orasida bo'lishi kerak")
        private String username;

        @NotBlank(message = "Parol bo'sh bo'lmasligi kerak")
        @Size(min = 6, max = 100, message = "Parol 6-100 belgi orasida bo'lishi kerak")
        private String password;
    }
}