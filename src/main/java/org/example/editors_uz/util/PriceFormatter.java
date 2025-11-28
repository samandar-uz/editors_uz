package org.example.editors_uz.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceFormatter {

    public static String format(String price) {
        if (price == null || price.equals("0")) {
            return "Bepul";
        }
        try {
            long amount = Long.parseLong(price);
            return String.format("%,d UZS", amount).replace(',', ' ');
        } catch (NumberFormatException e) {
            return price + " UZS";
        }
    }

    public String formatAmount(BigDecimal amount) {
        return String.format("%,d", amount.longValue()).replace(',', ' ');
    }
}