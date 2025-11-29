package org.example.editors_uz.util;

import org.example.editors_uz.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
@Component
public record SendFile(ReadErrorResponse readErrorResponse, WriteMultipart writeMultipart) {
    private static final String BOUNDARY_PREFIX = "----WebKitFormBoundary";

    public void sendFileRequest(String urlString, MultipartFile file, String message, User user, BigDecimal amount) throws IOException {
        String boundary = BOUNDARY_PREFIX + System.currentTimeMillis();

        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        try (OutputStream out = conn.getOutputStream()) {
            writeMultipart.writeMultipartData(out, boundary, file, message, user,amount);
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException("Telegram API error: " + conn.getResponseCode() + " - " + readErrorResponse.readErrorResponse(conn));
        }
    }
}
