package org.example.editors_uz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.editors_uz.dto.ValidationResult;
import org.example.editors_uz.util.PriceFormatter;
import org.example.editors_uz.util.SendFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PriceFormatter priceFormatter;
    private final SendFile sendFileRequest;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/jpg", "application/pdf"
    );

    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendDocument";


    @Value("${telegram.bot.token}")
    private String botToken;


    @Value("${payment.min-amount:1000}")
    private BigDecimal minAmount;

    @Value("${payment.max-amount:100000000}")
    private BigDecimal maxAmount;

    @Value("${payment.max-file-size:5242880}") // 5MB
    private long maxFileSize;


    public void sendFileToTelegram(MultipartFile file, String message) throws IOException {
        String urlString = String.format(TELEGRAM_API_URL, botToken);

        IOException lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                sendFileRequest.sendFileRequest(urlString, file, message);
                log.debug("File successfully sent to Telegram on attempt {}", attempt);
                return;
            } catch (IOException e) {
                lastException = e;
                log.warn("Error sending file to Telegram (attempt {}): {}", attempt, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    sleepAttempt(attempt);
                }
            }
        }
        throw new IOException("Failed to send file after " + MAX_RETRIES + " attempts", lastException);
    }

    public ValidationResult validatePayment(BigDecimal amount, MultipartFile file) {
        if (amount == null || amount.compareTo(minAmount) < 0) {
            return ValidationResult.error("⚠️ Minimal to'lov summasi " + priceFormatter.formatAmount(minAmount) + " UZS");
        }
        if (amount.compareTo(maxAmount) > 0) {
            return ValidationResult.error("⚠️ Maksimal summa " + priceFormatter.formatAmount(maxAmount) + " UZS");
        }
        if (file == null || file.isEmpty()) {
            return ValidationResult.error("📎 Chek fayli yuklanmadi!");
        }
        if (file.getSize() > maxFileSize) {
            return ValidationResult.error(
                    String.format("📎 Fayl hajmi %.1f MB dan oshmasligi kerak", maxFileSize / 1_048_576.0)
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            return ValidationResult.error("📎 Faqat JPG, PNG yoki PDF formatdagi fayllar qabul qilinadi");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..") || filename.length() > 255) {
            return ValidationResult.error("📎 Fayl nomi noto'g'ri");
        }

        return ValidationResult.success();
    }

    private void sleepAttempt(int attempt) {
        try {
            Thread.sleep((long) RETRY_DELAY_MS * attempt);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Retry sleep interrupted", ie);
        }
    }
}
