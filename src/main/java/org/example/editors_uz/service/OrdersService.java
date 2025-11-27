package org.example.editors_uz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.entity.Orders;
import org.example.editors_uz.entity.Templates;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.exception.DuplicateResourceException;
import org.example.editors_uz.exception.ResourceNotFoundException;
import org.example.editors_uz.repository.OrdersRepository;
import org.example.editors_uz.repository.TemplatesRepository;
import org.example.editors_uz.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final TemplatesRepository templatesRepository;

    @Transactional
    public Orders createOrder(Integer userId, Integer templateId) {
        log.info("Buyurtma yaratish boshlandi: userId={}, templateId={}", userId, templateId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User topilmadi! ID: " + userId));

        Templates template = templatesRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template topilmadi! ID: " + templateId));

        if (ordersRepository.existsByUserIdAndTemplateId(userId, templateId)) {
            throw new DuplicateResourceException("Siz bu kursni allaqachon sotib olgansiz!");
        }

        Orders order = Orders.builder()
                .user(user)
                .template(template)
                .build();

        Orders savedOrder = ordersRepository.save(order);
        log.info("Buyurtma muvaffaqiyatli yaratildi: orderId={}", savedOrder.getId());

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Orders> getUserOrders(Integer userId) {
        log.info("Foydalanuvchi buyurtmalari yuklanmoqda: userId={}", userId);
        return ordersRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserPurchased(Integer userId, Integer templateId) {
        return ordersRepository.existsByUserIdAndTemplateId(userId, templateId);
    }
}

