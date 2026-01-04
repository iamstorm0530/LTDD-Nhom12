package com.example.clothshop.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    public static String format(double value) {
        return NumberFormat
                .getInstance(new Locale("vi", "VN"))
                .format(value) + " ₫";
    }
}
