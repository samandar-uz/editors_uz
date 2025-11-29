package org.example.editors_uz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.entity.Orders;
import org.example.editors_uz.entity.Product;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.exception.DuplicateResourceException;
import org.example.editors_uz.exception.InsufficientBalanceException;
import org.example.editors_uz.exception.ResourceNotFoundException;
import org.example.editors_uz.repository.OrdersRepository;
import org.example.editors_uz.repository.ProductRepository;
import org.example.editors_uz.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String createOrder(Integer userId, Integer productId) {

        log.info("Creating order: userId={}, productId={}", userId, productId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User topilmadi! ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product topilmadi! ID: " + productId));

        if (ordersRepository.existsByUserIdAndProductId(userId, productId)) {
            log.warn("Duplicate order attempt: userId={}, productId={}", userId, productId);
            throw new DuplicateResourceException("Siz bu kursni allaqachon sotib olgansiz!");
        }

        if (product.getPrice() > 0) {
            if (user.getSalary() < product.getPrice()) {
                log.warn("Insufficient balance: userId={}, balance={}, required={}", userId, user.getSalary(), product.getPrice());
                throw new InsufficientBalanceException(String.format("Balansingiz yetarli emas! Kerak: %d UZS, Mavjud: %d UZS", product.getPrice(), user.getSalary()));
            }

            user.setSalary(user.getSalary() - product.getPrice());
            userRepository.save(user);
            log.info("User balans yangilandi: userId={}, newBalance={}", userId, user.getSalary());
        }

        Orders order = Orders.builder()
                .user(user)
                .product(product)
                .build();

        Orders savedOrder = ordersRepository.save(order); // UUID shu paytda qo‘shiladi

        log.info("Order yaratildi: orderId={}, accessUuid={}", savedOrder.getId(), savedOrder.getAccessUuid());
        return savedOrder.getAccessUuid();  // <-- TO‘G‘RI YER
    }

    public boolean existsByUserIdAndProductId(Integer userId, Integer productId) {
        return ordersRepository.existsByUserIdAndProductId(userId, productId);
    }

    public String getAccessUuid(Integer userId, Integer productId) {
        return ordersRepository.findByUserIdAndProductId(userId, productId)
                .map(Orders::getAccessUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Buyurtma topilmadi!"));
    }
}
