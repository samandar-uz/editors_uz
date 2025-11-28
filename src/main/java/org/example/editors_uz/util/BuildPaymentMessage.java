package org.example.editors_uz.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Component
public record BuildPaymentMessage(Escape escape, PriceFormatter formatter) {
    public String buildPaymentMessage(BigDecimal amount, String comment, Integer id) {
        String formattedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        return String.format("""
                        💳 <b>Yangi to'lov kelib tushdi!</b>
                        
                        👤 <b>ID:</b> %s
                        💰 <b>Summa:</b> %s UZS
                        %s🕒 <b>Sana:</b> %s
                        """,
                id.toString(),
                formatter.formatAmount(amount),
                (comment != null && !comment.isBlank()) ? "📝 <b>Izoh:</b> " + escape.escapeHtml(comment) + "\n" : "",
                formattedTime
        );
    }
}
