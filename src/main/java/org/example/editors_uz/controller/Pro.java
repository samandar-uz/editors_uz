package org.example.editors_uz.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.entity.Attachment;
import org.example.editors_uz.entity.Product;
import org.example.editors_uz.repository.ProductRepository;
import org.example.editors_uz.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class Pro {
    private final FileService fileService;
    private final ProductRepository productRepository;

    @Transactional
    @PostMapping("/products")
    public String addProduct(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Yangi product qo'shish: name={}, price={}", name, price);

            // Validatsiya
            if (photo == null || photo.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Rasm tanlash majburiy!");
                return "redirect:/add";
            }

            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Nomi kiritilishi shart!");
                return "redirect:/add";
            }

            // Rasm saqlash
            Attachment attachment = fileService.saveFile(photo);
            log.info("Rasm saqlandi: attachmentId={}", attachment.getId());

            // Product yaratish
            Product product = new Product();
            product.setPhoto(attachment);
            product.setName(name.trim());
            product.setDescription(description != null ? description.trim() : "");
            product.setPrice(Integer.valueOf(price != null ? price.trim() : "0"));

            productRepository.save(product);
            log.info("Product saqlandi: productId={}", product.getId());

            redirectAttributes.addFlashAttribute("success", "Kurs muvaffaqiyatli qo'shildi!");
            return "redirect:/courses";

        } catch (Exception e) {
            log.error("Product qo'shishda xatolik: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Xatolik yuz berdi: " + e.getMessage());
            return "redirect:/add";
        }
    }
}