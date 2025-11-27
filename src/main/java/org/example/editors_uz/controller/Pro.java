package org.example.editors_uz.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.example.editors_uz.dto.ProductDTO;
import org.example.editors_uz.entity.Attachment;
import org.example.editors_uz.entity.Product;
import org.example.editors_uz.repository.ProductRepository;
import org.example.editors_uz.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller


@RequiredArgsConstructor
public class Pro {
    private final FileService fileService;
    private final ProductRepository productRepository;

    @Transactional
    @PostMapping("/products")
    public String addProduct(@RequestParam MultipartFile photo, @ModelAttribute ProductDTO productDTO) {
        Attachment attachment = fileService.saveFile(photo);
        Product product = new Product();
        product.setPhoto(attachment);
        product.setName(productDTO.name());
        product.setPrice(productDTO.price());
        product.setDescription(productDTO.description())        ;
        productRepository.save(product);
        return "redirect:/index";
    }


}
