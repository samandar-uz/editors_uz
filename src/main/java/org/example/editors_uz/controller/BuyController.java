package org.example.editors_uz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.entity.Product;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.service.OrdersService;
import org.example.editors_uz.repository.ProductRepository;
import org.example.editors_uz.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/course")
public class BuyController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrdersService ordersService;

    @GetMapping("/buy/{templateId}")
    public String buyCoursePage(@PathVariable Integer templateId,
                                @CookieValue(value = "AUTH_TOKEN", required = false) String token,
                                Model model) {
        try {
            // 🔑 1. Token orqali userni olish
            User user = userRepository.findByKey(token)
                    .orElseThrow(() -> new RuntimeException("User topilmadi!"));

            // 📦 2. Kursni topish
            Product product = productRepository.findById(templateId)
                    .orElseThrow(() -> new RuntimeException("Product topilmadi!"));

            log.info("⛳ Kurs sotib olish sahifasi ochildi: user={}, template={}, price={}, userBalance={}",
                    user.getUsername(), product.getName(), product.getPrice(), user.getSalary());

            // 🧾 3. Oldin sotib olganmi?
            boolean alreadyPurchased = ordersService.existsByUserIdAndProductId(user.getId(), templateId);
            if (alreadyPurchased) {
                String accessUuid = ordersService.getAccessUuid(user.getId(), templateId);

                log.warn("⚠ Kurs allaqachon sotib olingan: user={}, course={}", user.getUsername(), product.getName());

                model.addAttribute("type", "ALREADY_PURCHASED");
                model.addAttribute("courseName", product.getName());
                model.addAttribute("access", accessUuid);
                model.addAttribute("coursePrice", product.getPrice());
                return "course-buy";
            }

            // 💳 4. Agar balans yetarli bo‘lsa, sotib olish
            if (user.getSalary() >= product.getPrice()) {
                Integer oldBalance = user.getSalary();
                Integer newBalance = oldBalance - product.getPrice();

                // 🔥 Order yaratish va UUID olish
                String accessUuid = ordersService.createOrder(user.getId(), templateId);

                // 💾 Yangi balansni saqlash
                user.setSalary(newBalance);
                userRepository.save(user);

                log.info("✅ Kurs sotib olindi: oldBalance={}, newBalance={}, accessUuid={}", oldBalance, newBalance, accessUuid);

                // Front-end uchun maʼlumotlar
                model.addAttribute("success", true);
                model.addAttribute("type", "COURSE_PURCHASE_SUCCESS");
                model.addAttribute("courseName", product.getName());
                model.addAttribute("coursePrice", product.getPrice());
                model.addAttribute("oldBalance", oldBalance);
                model.addAttribute("newBalance", newBalance);
                model.addAttribute("access", accessUuid);

            } else {
                // ❌ Balans yetmaydi
                Integer needed = product.getPrice() - user.getSalary();
                log.warn("❌ Balans yetarli emas: need={}, has={}, required={}", needed, user.getSalary(), product.getPrice());

                model.addAttribute("error", "Balansingiz yetarli emas!");
                model.addAttribute("type", "BALANCE_INSUFFICIENT");
                model.addAttribute("courseName", product.getName());
                model.addAttribute("coursePrice", product.getPrice());
                model.addAttribute("userBalance", user.getSalary());
                model.addAttribute("needed", needed);
            }

            return "course-buy";

        } catch (Exception e) {
            log.error("❗️ Kurs sotib olishda xatolik: {}", e.getMessage(), e);
            model.addAttribute("error", "Xatolik yuz berdi: " + e.getMessage());
            model.addAttribute("type", "PURCHASE_ERROR");
            return "course-buy";
        }
    }
}
