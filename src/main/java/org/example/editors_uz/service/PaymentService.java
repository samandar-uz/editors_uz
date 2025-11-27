package org.example.editors_uz.service;

import okhttp3.*;
import org.example.editors_uz.dto.ValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class PaymentService {

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/jpg", "application/pdf"
    );

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.admin.chat-id}")
    private String adminChatId;

    @Value("${payment.min-amount:1000}")
    private BigDecimal minAmount;

    @Value("${payment.max-file-size:5242880}") // 5MB
    private long maxFileSize;

    public String buildPaymentMessage(BigDecimal amount, String comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String formattedTime = LocalDateTime.now().format(formatter);

        StringBuilder message = new StringBuilder();
        message.append("💳 *Yangi to'lov kelib tushdi!*\n\n");
        message.append("📌 *Summa:* ").append(formatAmount(amount)).append(" UZS\n");

        if (comment != null && !comment.trim().isEmpty()) {
            message.append("📝 *Izoh:* ").append(escapeMarkdown(comment)).append("\n");
        }

        message.append("🕒 *Sana:* ").append(formattedTime);

        return message.toString();
    }

    private String formatAmount(BigDecimal amount) {
        return String.format("%,d", amount.longValue());
    }

    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replaceAll("([_*\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");
    }

    public void sendFileToTelegram(MultipartFile file) throws IOException {
        String urlString = "https://api.telegram.org/bot" + botToken + "/sendDocument";
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            try (OutputStream out = conn.getOutputStream()) {
                writeMultipartData(out, boundary, file);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                String errorMsg = readErrorResponse(conn);
                throw new IOException("Chek yuborishda xatolik: " + responseCode + " - " + errorMsg);
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void writeMultipartData(OutputStream out, String boundary, MultipartFile file)
            throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);

        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n");
        writer.append(adminChatId).append("\r\n");
        writer.flush();

        // Fayl qo'shish
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"document\"; filename=\"")
                .append(file.getOriginalFilename()).append("\"\r\n");
        writer.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
        writer.flush();

        file.getInputStream().transferTo(out);
        out.flush();

        writer.append("\r\n");
        writer.append("--").append(boundary).append("--\r\n");
        writer.flush();
    }

    public void sendTextToTelegram(String message) throws IOException {
        if (message == null || message.isBlank()) return;

        message = sanitizeMessage(message); // xavfsiz forma

        String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        OkHttpClient client = new OkHttpClient();

        // Body JSON tarzda yuboriladi (url emas!)
        String json = "{"
                + "\"chat_id\":\"" + adminChatId + "\","
                + "\"text\":\"" + escapeJson(message) + "\","
                + "\"parse_mode\":\"HTML\"" // HTML ishlatyapmiz, Markdowndan xavfsizroq
                + "}";

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                throw new IOException("Telegram API xatolik: " + response.code() + " - " + response.body().string());
            }
        }
    }


    private String readErrorResponse(HttpURLConnection conn) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            return "Noma'lum xatolik";
        }
    }

    public ValidationResult validatePayment(BigDecimal amount, MultipartFile file) {
        if (amount == null || amount.compareTo(minAmount) < 0) {
            return ValidationResult.error(
                    String.format("⚠ Minimal to'lov summasi %s UZS", minAmount)
            );
        }

        if (amount.compareTo(BigDecimal.valueOf(100_000_000)) > 0) {
            return ValidationResult.error("⚠ Maksimal summa 100,000,000 UZS");
        }
        if (file == null || file.isEmpty()) {
            return ValidationResult.error("📎 Chek fayli yuklanmadi!");
        }

        if (file.getSize() > maxFileSize) {
            return ValidationResult.error(
                    String.format("📎 Fayl hajmi %d MB dan oshmasligi kerak", maxFileSize / 1_048_576)
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            return ValidationResult.error(
                    "📎 Faqat JPG, PNG yoki PDF formatdagi fayllar qabul qilinadi"
            );
        }

        return ValidationResult.success();
    }

    private String sanitizeMessage(String message) {
        return message.replaceAll("[\\n\\r]+", " ").trim();
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

}
