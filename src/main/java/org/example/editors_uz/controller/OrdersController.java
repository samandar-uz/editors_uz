package org.example.editors_uz.controller;

import lombok.RequiredArgsConstructor;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.UserRepository;
import org.example.editors_uz.service.OrdersService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(String token) {
        return userRepository.findByKey(token).orElseThrow(() -> new RuntimeException("User topilmadi!"));
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam("templateId") Integer templateId, @CookieValue(value = "AUTH_TOKEN", required = false) String token, RedirectAttributes redirectAttributes) {
        try {
            if (token == null || userRepository.findByKey(token).isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Iltimos, tizimga kiring!");
                return "redirect:/auth";
            }

            User user = getAuthenticatedUser(token);
            ordersService.createOrder(user.getId(), templateId);

            redirectAttributes.addFlashAttribute("success", "Kurs muvaffaqiyatli sotib olindi!");
            return "redirect:/basket";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/templates";
        }
    }
}
