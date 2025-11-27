package org.example.editors_uz.service;

import lombok.RequiredArgsConstructor;
import org.example.editors_uz.entity.Orders;
import org.example.editors_uz.entity.Templates;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.repository.OrdersRepository;
import org.example.editors_uz.repository.TemplatesRepository;
import org.example.editors_uz.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final TemplatesRepository templatesRepository;

    @Transactional
    public void createOrder(Integer userId, Integer templateId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User topilmadi! ID: " + userId));

        Templates template = templatesRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template topilmadi! ID: " + templateId));
        if (ordersRepository.existsByUserIdAndTemplateId(userId, templateId)) {
            throw new RuntimeException("Siz bu kursni allaqachon sotib olgansiz!");
        }

        Orders order = Orders.builder()
                .user(user)
                .template(template)
                .build();

        ordersRepository.save(order);
    }

}