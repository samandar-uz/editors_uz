package org.example.editors_uz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.dto.ValidationResult;
import org.example.editors_uz.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

   private final PaymentService paymentService;




    @PostMapping("/payment/submit")
    public String submitPayment(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("checkFile") MultipartFile checkFile,
            @RequestParam(value = "comment", required = false) String comment,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ValidationResult validation = paymentService.validatePayment(amount, checkFile);
            if (!validation.valid()) {
                redirectAttributes.addFlashAttribute("error", validation.errorMessage());
                return "redirect:/balance/add";
            }
            String message = paymentService.buildPaymentMessage(amount, comment);
            paymentService.sendTextToTelegram(message);
            paymentService.sendFileToTelegram(checkFile);
            log.info("To'lov muvaffaqiyatli yuborildi. Summa: {}", amount);
            redirectAttributes.addFlashAttribute("success",
                    "✔ To'lovingiz qabul qilindi! Tez orada tekshirib ko'ramiz.");

            return "redirect:/";

        } catch (IOException e) {
            log.error("To'lov yuborishda xatolik: ", e);
            redirectAttributes.addFlashAttribute("error",
                    "❌ Xatolik yuz berdi. Iltimos, qaytadan urinib ko'ring.");
            return "redirect:/balance/add";
        }
    }








}