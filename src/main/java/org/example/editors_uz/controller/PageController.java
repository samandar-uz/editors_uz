package org.example.editors_uz.controller;

import lombok.RequiredArgsConstructor;
import org.example.editors_uz.entity.OrderType;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.ProductRepository;
import org.example.editors_uz.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(String token) {
        return token == null ? null : userRepository.findByKey(token).orElse(null);
    }

    @GetMapping
    public String getHomePage(@CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        return getAuthenticatedUser(token) == null ? "redirect:/auth" : "index";
    }

    @GetMapping("/index")
    public String indexPage(@CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        return getAuthenticatedUser(token) == null ? "redirect:/auth" : "index";
    }

    @GetMapping("/courses")
    public String getCourses(Model model, @CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        if (getAuthenticatedUser(token) == null) return "redirect:/auth";
        model.addAttribute("products", productRepository.findAllByOrderType(OrderType.PRODUCT));
        return "courses";
    }

    @GetMapping("/templates")
    public String getTemplates(Model model, @CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        User user = getAuthenticatedUser(token);
        if (user == null) return "redirect:/auth";
        model.addAttribute("user", user);
        model.addAttribute("products", productRepository.findAllByOrderType(OrderType.TEMPLATE));
        return "templates";
    }


    @GetMapping("/auth")
    public String authPage(@CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        if (token != null && userRepository.findByKey(token).isPresent()) {
            return "redirect:/index";
        }
        return "auth";
    }
    @GetMapping("/add")
    public String addProduct() {
        return "add-product";
    }

    @GetMapping("/course/access/{uuid}")
    public String access(@PathVariable String uuid) {
        return "redirect:https://t.me/UzTurbo_bot?start=" + uuid;
    }
    @GetMapping("/balance/add")
    public String addBalancePage(@AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/auth";
        }
        return "add-balance";
    }
}
