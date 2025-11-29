package org.example.editors_uz.util;

import lombok.RequiredArgsConstructor;
import org.example.editors_uz.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
@RequiredArgsConstructor
@Component
public class WriteMultipart {
    @Value("${telegram.admin.chat-id}")
    private String adminChatId;
    private final WriteField writeField;


    private final Sanitize  sanitize;

    public void writeMultipartData(OutputStream out, String boundary, MultipartFile file, String message, User user, BigDecimal amount)
            throws IOException {

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true)) {

            writeField.writeField(writer, boundary, "chat_id", adminChatId);
            writeField.writeField(writer, boundary, "caption", message);
            writeField.writeField(writer, boundary, "parse_mode", "HTML");
            String markup = """
        {
          "inline_keyboard": [[
            { "text": "✔ Tasdiqlash", "callback_data": "pay:confirm:%s|%s" },
            { "text": "❌ Bekor qilish", "callback_data": "pay:cancel:%s|%s" }
          ]]
        }
        """.formatted(user.getId(), amount, user.getId(), amount);

            writeField.writeField(writer, boundary, "reply_markup", markup);


            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"document\"; filename=\"")
                    .append(sanitize.sanitizeFilename(file.getOriginalFilename())).append("\"\r\n");
            writer.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
            writer.flush();

            file.getInputStream().transferTo(out);
            out.flush();

            writer.append("\r\n").append("--").append(boundary).append("--\r\n");
        }
    }
}
