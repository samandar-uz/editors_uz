package org.example.editors_uz.util;

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

    public static boolean isFree(String price) {
        return price == null || price.equals("0");
    }
}