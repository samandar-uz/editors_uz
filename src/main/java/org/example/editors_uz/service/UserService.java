package org.example.editors_uz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.entity.User;
import org.example.editors_uz.exception.ResourceNotFoundException;
import org.example.editors_uz.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final HomeService homeService;

    @Transactional
    public User registerUser(String username, String password) {
        log.info("Yangi foydalanuvchi ro'yxatdan o'tmoqda: username={}", username);

        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("Foydalanuvchi nomi band: username={}", username);
            throw new RuntimeException("Bu foydalanuvchi nomi band!");
        }

        String token = homeService.generateToken();
        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .key(token)
                .enabled(true)
                .role("USER")
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Foydalanuvchi muvaffaqiyatli ro'yxatdan o'tdi: userId={}", savedUser.getId());

        return savedUser;
    }

    @Transactional
    public User authenticateUser(String username, String password) {
        log.info("Foydalanuvchi autentifikatsiyasi: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi!"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Parol noto'g'ri: username={}", username);
            throw new RuntimeException("Parol noto'g'ri!");
        }

        if (!user.isEnabled()) {
            log.warn("Profil bloklangan: username={}", username);
            throw new RuntimeException("Profil bloklangan!");
        }

        String newToken = homeService.generateToken();
        user.setKey(newToken);
        userRepository.save(user);

        log.info("Foydalanuvchi muvaffaqiyatli tizimga kirdi: userId={}", user.getId());
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByToken(String token) {
        return userRepository.findByKey(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token yaroqsiz!"));
    }
}