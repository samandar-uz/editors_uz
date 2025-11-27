package org.example.editors_uz.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.UserRepository;
import org.example.editors_uz.service.HomeService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response,
            Model model) {

        try {
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                if (!passwordEncoder.matches(password, user.getPassword())) {
                    model.addAttribute("errorMessage", "❌ Parol noto'g'ri!");
                    return "auth";
                }
                if (!user.isEnabled()) {
                    model.addAttribute("errorMessage", "🚫 Profil bloklangan!");
                    return "auth";
                }

                String token = homeService.generateToken();
                user.setKey(token);
                userRepository.save(user);
                homeService.addAuthCookie(response, token);

                log.info("Foydalanuvchi tizimga kirdi: {}", username);

            } else {
                User newUser = new User();
                String token = homeService.generateToken();
                newUser.setUsername(username);
                newUser.setPassword(passwordEncoder.encode(password));
                newUser.setKey(token);
                newUser.setEnabled(true);
                newUser.setRole("USER");
                userRepository.save(newUser);
                homeService.addAuthCookie(response, token);
                log.info("Yangi foydalanuvchi ro'yxatdan o'tdi: {}", username);

            }
            return "redirect:/index";

        } catch (Exception e) {
            log.error("Autentifikatsiya xatosi: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Xatolik yuz berdi: " + e.getMessage());
            return "auth";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        homeService.removeAuthCookie(response);
        log.info("Foydalanuvchi tizimdan chiqdi");
        return "redirect:/auth";
    }
}