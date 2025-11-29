package org.example.editors_uz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.dto.ValidationResult;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.UserRepository;
import org.example.editors_uz.service.PaymentService;
import org.example.editors_uz.util.BuildPaymentMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final BuildPaymentMessage buildPaymentMessage;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping("/payment/submit")
    public String submitPayment(@RequestParam("amount") BigDecimal amount, @RequestParam("checkFile") MultipartFile checkFile, @RequestParam(value = "comment", required = false) String comment, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        try {
            ValidationResult validation = paymentService.validatePayment(amount, checkFile);
            if (!validation.valid()) {
                redirectAttributes.addFlashAttribute("error", validation.errorMessage());
                return "redirect:/balance/add";
            }
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "❌ Tizimga kirish talab qilinadi");
                return "redirect:/auth";
            }

            user = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User topilmadi"));
            String message = buildPaymentMessage.buildPaymentMessage(amount, comment, user.getId());
            paymentService.sendFileToTelegram(checkFile, message,user,amount);
            log.info("Payment submitted successfully: user={}, amount={}", user.getUsername(), amount);
            redirectAttributes.addFlashAttribute("success", "✔️ To'lovingiz qabul qilindi! Tez orada tekshirib ko'ramiz.");
            return "redirect:/";

        } catch (IOException e) {
            log.error("Payment submission failed: user={}, error={}", user.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "❌ Xatolik yuz berdi. Iltimos, qaytadan urinib ko'ring.");
            return "redirect:/balance/add";
        } catch (Exception e) {
            log.error("Unexpected error during payment: ", e);
            redirectAttributes.addFlashAttribute("error", "❌ Kutilmagan xatolik yuz berdi.");
            return "redirect:/balance/add";
        }
    }

}