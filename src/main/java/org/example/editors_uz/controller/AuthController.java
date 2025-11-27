package org.example.editors_uz.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.UserRepository;
import org.example.editors_uz.service.HomeService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final HomeService homeService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @PostMapping("/auth")
    public String handleLoginOrRegister(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response, Model model) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                model.addAttribute("errorMessage", "❌ Parol noto‘g‘ri!");
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
            return "redirect:/index";
        }
        User newUser = new User();
        String token = homeService.generateToken();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setKey(token);
        newUser.setEnabled(true);
        userRepository.save(newUser);
        homeService.addAuthCookie(response, token);
        model.addAttribute("successMessage", "🎉 Profil yaratildi va login qilindi!");
        return "redirect:/index";
    }
}
