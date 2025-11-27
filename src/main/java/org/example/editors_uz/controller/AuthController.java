package org.example.editors_uz.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.dto.AuthRequest;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.service.HomeService;
import org.example.editors_uz.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final HomeService homeService;

    @PostMapping("/auth")
    public String handleLoginOrRegister(
            @Valid @ModelAttribute AuthRequest request,
            BindingResult bindingResult,
            HttpServletResponse response,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Ma'lumotlarni to'g'ri kiriting!");
            return "auth";
        }

        try {
            User user;
            try {
                user = userService.authenticateUser(request.getUsername(), request.getPassword());
                log.info("Foydalanuvchi tizimga kirdi: {}", request.getUsername());
            } catch (Exception e) {
                user = userService.registerUser(request.getUsername(), request.getPassword());
                log.info("Yangi foydalanuvchi ro'yxatdan o'tdi: {}", request.getUsername());
                model.addAttribute("successMessage", "🎉 Profil yaratildi va login qilindi!");
            }

            homeService.addAuthCookie(response, user.getKey());
            return "redirect:/index";

        } catch (Exception e) {
            log.error("Autentifikatsiya xatosi: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "auth";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        homeService.removeAuthCookie(response);
        return "redirect:/auth";
    }
}