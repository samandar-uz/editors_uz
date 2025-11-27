


 package org.example.editors_uz.controller;

 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;
 import org.example.editors_uz.entity.Product;
 import org.example.editors_uz.entity.User;
 import org.example.editors_uz.repository.ProductRepository;
 import org.example.editors_uz.repository.UserRepository;
 import org.example.editors_uz.service.OrdersService;
 import org.springframework.stereotype.Controller;
 import org.springframework.ui.Model;
 import org.springframework.web.bind.annotation.*;

 @Controller
 @RequiredArgsConstructor
 @Slf4j
 @RequestMapping("/course")
 public class CourseBuyController {

 private final UserRepository userRepository;
 private final ProductRepository templatesRepository;
 private final OrdersService ordersService;

 @GetMapping("/buy/{templateId}")
 public String buyCoursePage(
 @PathVariable Integer templateId,
 @CookieValue(value = "AUTH_TOKEN", required = false) String token,
 Model model) {

 try {
 // User tekshirish
 if (token == null) {
 return "redirect:/auth";
 }

 User user = userRepository.findByKey(token)
 .orElseThrow(() -> new RuntimeException("User topilmadi!"));

     Product product = templatesRepository.findById(templateId)
             .orElseThrow(() -> new RuntimeException("Product topilmadi!"));


 log.info("Sotib olish sahifasi: user={}, template={}, price={}, userBalance={}",
 user.getUsername(), product.getName(), product.getPrice(), user.getSalary());

 // Balans tekshirish
 if (user.getSalary() >= product.getPrice()) {
 // Balans yetarli - sotib olish
 Integer oldBalance = user.getSalary();
 Integer newBalance = oldBalance - product.getPrice();

 // Order yaratish
 ordersService.createOrder(user.getId(), templateId);

 // Balansni yangilash
 user.setSalary(newBalance);
 userRepository.save(user);

 log.info("Kurs muvaffaqiyatli sotib olindi: orderId created, oldBalance={}, newBalance={}",
 oldBalance, newBalance);

 // Success ma'lumotlari
 model.addAttribute("success", true);
 model.addAttribute("courseName", product.getName());
 model.addAttribute("coursePrice", product.getPrice());
 model.addAttribute("oldBalance", oldBalance);
 model.addAttribute("newBalance", newBalance);

 } else {
 // Balans yetarli emas
 Integer needed = product.getPrice() - user.getSalary();

 log.warn("Balans yetarli emas: need={}, has={}, required={}",
 needed, user.getSalary(), product.getPrice());

 model.addAttribute("error", "Balansingiz yetarli emas!");
 model.addAttribute("courseName", product.getName());
 model.addAttribute("coursePrice", product.getPrice());
 model.addAttribute("userBalance", user.getSalary());
 model.addAttribute("needed", needed);
 }

 return "course-buy";

 } catch (Exception e) {
 log.error("Kurs sotib olishda xatolik: {}", e.getMessage(), e);
 model.addAttribute("error", "Xatolik yuz berdi: " + e.getMessage());
 return "course-buy";
 }
 }
 }