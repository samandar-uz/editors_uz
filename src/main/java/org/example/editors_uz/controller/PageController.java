package org.example.editors_uz.controller;

import lombok.RequiredArgsConstructor;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.OrdersRepository;
import org.example.editors_uz.repository.ProductRepository;
import org.example.editors_uz.repository.TemplatesRepository;
import org.example.editors_uz.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TemplatesRepository templatesRepository;
    private final OrdersRepository ordersRepository;

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
        model.addAttribute("products", productRepository.findAll());
        return "courses";
    }

    @GetMapping("/templates")
    public String getTemplates(Model model, @CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        User user = getAuthenticatedUser(token);
        if (user == null) return "redirect:/auth";

        model.addAttribute("user", user);
        model.addAttribute("templates", templatesRepository.findAll());
        return "templates";
    }

    @GetMapping("/basket")
    public String getBasket(@CookieValue(value = "AUTH_TOKEN", required = false) String token, Model model) {
        User user = getAuthenticatedUser(token);
        if (user == null) return "redirect:/auth";

        model.addAttribute("orders", ordersRepository.findByUser(user)); // faqat userning buyurtmalari
        return "basket";
    }

    @GetMapping("/auth")
    public String loginPage(@CookieValue(value = "AUTH_TOKEN", required = false) String token) {
        return getAuthenticatedUser(token) != null ? "redirect:/index" : "auth";
    }
}
